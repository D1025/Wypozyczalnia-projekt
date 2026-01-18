package com.projekt.wypozyczalnia.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.projekt.wypozyczalnia.dto.common.ErrorResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
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

import jakarta.servlet.http.HttpServletResponse;

import java.time.OffsetDateTime;

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
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                // public endpoints
                .requestMatchers("/actuator/health", "/actuator/info", "/auth/**").permitAll()

                // BOOKS
                // everyone logged in can list/view books
                .requestMatchers(HttpMethod.GET, "/api/books/**").hasAnyRole("MEMBER", "ASSISTANT", "LIBRARIAN")
                // only staff can modify books
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
        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            objectMapper.writeValue(response.getOutputStream(), payload);
        } catch (Exception ex) {
            throw new IllegalStateException("Nie udało się zbudować odpowiedzi błędu", ex);
        }
    }
}
