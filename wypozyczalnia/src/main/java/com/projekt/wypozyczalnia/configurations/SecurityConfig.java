package com.projekt.wypozyczalnia.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projekt.wypozyczalnia.dto.common.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import jakarta.servlet.DispatcherType;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import jakarta.servlet.http.HttpServletResponse;

import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final Converter<Jwt, java.util.Collection<GrantedAuthority>> keycloakRoleConverter;
    private final ObjectMapper objectMapper;

    public SecurityConfig(@Value("${keycloak.resource:wypozyczalnia-app}") String clientId,
                          ObjectMapper objectMapper) {
        this.keycloakRoleConverter = new KeycloakRealmRoleConverter(clientId);
        this.objectMapper = objectMapper;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            // apply to all requests
            .securityMatcher("/**")
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // public endpoints FIRST
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/actuator/health", "/actuator/info").permitAll()
                .requestMatchers("/error", "/favicon.ico", "/").permitAll()

                // Allow error dispatch and forward
                .dispatcherTypeMatchers(DispatcherType.ERROR, DispatcherType.FORWARD).permitAll()

                // BOOKS
                .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyRole("MEMBER", "ASSISTANT", "LIBRARIAN")
                .requestMatchers(HttpMethod.POST, "/api/books/**").hasAnyRole("ASSISTANT", "LIBRARIAN")
                .requestMatchers(HttpMethod.PUT, "/api/books/**").hasAnyRole("ASSISTANT", "LIBRARIAN")
                .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasRole("LIBRARIAN")

                // MEMBERS (admin/staff)
                .requestMatchers("/api/members/**").hasRole("LIBRARIAN")

                .anyRequest().authenticated())
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint((request, response, authException) ->
                    writeErrorResponse(response, HttpStatus.UNAUTHORIZED, "Brak autoryzacji", request.getRequestURI()))
                .accessDeniedHandler((request, response, accessDeniedException) ->
                    writeErrorResponse(response, HttpStatus.FORBIDDEN, "Brak uprawnień do zasobu", request.getRequestURI())))
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
            .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*")); // Lub konkretne domeny dla bezpieczeństwa
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(keycloakRoleConverter);
        return converter;
    }

    private void writeErrorResponse(HttpServletResponse response, HttpStatus status, String message, String path) {
        ErrorResponseDto payload = ErrorResponseDto.builder()
                .timestamp(OffsetDateTime.now())
                .status(status.value())
                .error(status.getReasonPhrase())
                .message(message)
                .path(path)
                .build();

        try {
            response.resetBuffer();
        } catch (Exception ignored) {
            // ignore
        }

        response.setStatus(status.value());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        try {
            objectMapper.writeValue(response.getOutputStream(), payload);
        } catch (Exception ex) {
            // Fallback - do not throw any exception from error rendering.
            try {
                String minimal = String.format(
                        "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}",
                        payload.getTimestamp(),
                        payload.getStatus(),
                        safeJson(payload.getError()),
                        safeJson(payload.getMessage()),
                        safeJson(payload.getPath())
                );
                response.getWriter().write(minimal);
            } catch (Exception ignored) {
                // last resort: swallow
            }
        }
    }

    private static String safeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", " ").replace("\r", " ");
    }
}
