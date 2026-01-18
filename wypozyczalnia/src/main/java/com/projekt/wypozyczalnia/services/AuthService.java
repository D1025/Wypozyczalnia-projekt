package com.projekt.wypozyczalnia.services;

import com.projekt.wypozyczalnia.dto.auth.LoginRequestDto;
import com.projekt.wypozyczalnia.dto.auth.LogoutRequestDto;
import com.projekt.wypozyczalnia.dto.auth.RegisterRequestDto;
import com.projekt.wypozyczalnia.dto.auth.RegisterResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class AuthService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String keycloakBaseUrl;
    private final String realm;
    private final String clientId;
    private final String clientSecret;

    private final String keycloakAdminUsername;
    private final String keycloakAdminPassword;

    /**
     * Realm used to authenticate as Keycloak admin.
     *
     * By default Keycloak creates the bootstrap admin user in the "master" realm
     * (KEYCLOAK_ADMIN / KEYCLOAK_ADMIN_PASSWORD). The application realm (e.g. "wypozyczalnia")
     * usually won't contain that user, which results in LOGIN_ERROR user_not_found.
     */
    private final String keycloakAdminRealm;

    /** Optional admin-cli client secret (only if admin-cli is configured as confidential client). */
    private final String keycloakAdminClientSecret;

    public AuthService(
            @Value("${keycloak.auth-server-url}") String keycloakBaseUrl,
            @Value("${keycloak.realm}") String realm,
            @Value("${keycloak.resource}") String clientId,
            @Value("${keycloak.credentials.secret:}") String clientSecret,
            @Value("${keycloak.admin.username:admin}") String keycloakAdminUsername,
            @Value("${keycloak.admin.password:admin}") String keycloakAdminPassword,
            @Value("${keycloak.admin.realm:master}") String keycloakAdminRealm,
            @Value("${keycloak.admin.client-secret:}") String keycloakAdminClientSecret
    ) {
        this.keycloakBaseUrl = keycloakBaseUrl;
        this.realm = realm;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.keycloakAdminUsername = keycloakAdminUsername;
        this.keycloakAdminPassword = keycloakAdminPassword;
        this.keycloakAdminRealm = keycloakAdminRealm;
        this.keycloakAdminClientSecret = keycloakAdminClientSecret;
    }

    /**
     * Rejestracja realizowana przez Keycloak (endpoint publiczny w tym backendzie).
     *
     * Wymaga, aby w Keycloak:
     * - realm miał włączony "Direct Access Grants" dla klienta (już używasz password grant w Postmanie)
     * - użytkownik mógł zostać utworzony przez "admin-cli" (tu używamy password granta do admin-cli)
     */
    public RegisterResponseDto register(RegisterRequestDto request) {
        // 1) pobierz token admina (admin-cli)
        String adminToken = getAdminAccessToken();

        // 2) utwórz usera
        String createUserUrl = String.format("%s/admin/realms/%s/users", trimTrailingSlash(keycloakBaseUrl), realm);

        String role = (request.getRole() == null || request.getRole().isBlank()) ? "MEMBER" : request.getRole().trim();

        Map<String, Object> payload = Map.of(
                "username", request.getUsername(),
                "enabled", true,
                "email", request.getEmail(),
                "emailVerified", false,
                "credentials", new Object[]{
                        Map.of(
                                "type", "password",
                                "temporary", false,
                                "value", request.getPassword()
                        )
                }
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(adminToken);

        ResponseEntity<Void> createResp;
        try {
            createResp = restTemplate.exchange(createUserUrl, HttpMethod.POST, new HttpEntity<>(payload, headers), Void.class);
        } catch (HttpClientErrorException e) {
            throw e;
        }

        String location = createResp.getHeaders().getFirst(HttpHeaders.LOCATION);
        String userId = (location != null && location.contains("/")) ? location.substring(location.lastIndexOf('/') + 1) : null;

        // 3) nadaj rolę klienta (opcjonalnie). Jeżeli nie uda się, zwracamy usera bez roli.
        try {
            assignClientRoleToUser(userId, role, adminToken);
        } catch (Exception ignored) {
            // celowo ignorujemy, żeby rejestracja nie padała przez brak roli
        }

        return new RegisterResponseDto(userId, request.getUsername(), request.getEmail(), role);
    }

    /** Placeholder: w projekcie kolekcja Postmana ma /auth/login, /auth/refresh, /auth/logout.
     *  Ten serwis zostawia miejsce na ewentualne dopięcie tych funkcji, ale rejestracja działa niezależnie. */
    public Map<String, Object> login(LoginRequestDto request) {
        String tokenUrl = String.format(
                "%s/realms/%s/protocol/openid-connect/token",
                trimTrailingSlash(keycloakBaseUrl),
                realm
        );

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", clientId);

        // Jeżeli klient jest confidential, Keycloak wymaga client_secret.
        if (clientSecret != null && !clientSecret.isBlank()) {
            form.add("client_secret", clientSecret);
        }

        form.add("username", request.getUsername());
        form.add("password", request.getPassword());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> resp = restTemplate.exchange(tokenUrl, HttpMethod.POST, new HttpEntity<>(form, headers), Map.class);
        //noinspection unchecked
        return resp.getBody();
    }

    public Map<String, Object> refresh(Map<String, Object> request) {
        Object refreshToken = request != null ? request.get("refresh_token") : null;
        if (refreshToken == null || refreshToken.toString().isBlank()) {
            throw new IllegalArgumentException("refresh_token is required");
        }

        String tokenUrl = String.format(
                "%s/realms/%s/protocol/openid-connect/token",
                trimTrailingSlash(keycloakBaseUrl),
                realm
        );

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "refresh_token");
        form.add("client_id", clientId);
        if (clientSecret != null && !clientSecret.isBlank()) {
            form.add("client_secret", clientSecret);
        }
        form.add("refresh_token", refreshToken.toString());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> resp = restTemplate.exchange(tokenUrl, HttpMethod.POST, new HttpEntity<>(form, headers), Map.class);
        //noinspection unchecked
        return resp.getBody();
    }

    public void logout(LogoutRequestDto request) {
        if (request == null || request.getRefreshToken() == null || request.getRefreshToken().isBlank()) {
            throw new IllegalArgumentException("refreshToken is required");
        }

        String logoutUrl = String.format(
                "%s/realms/%s/protocol/openid-connect/logout",
                trimTrailingSlash(keycloakBaseUrl),
                realm
        );

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("client_id", clientId);
        if (clientSecret != null && !clientSecret.isBlank()) {
            form.add("client_secret", clientSecret);
        }
        form.add("refresh_token", request.getRefreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        restTemplate.exchange(logoutUrl, HttpMethod.POST, new HttpEntity<>(form, headers), Void.class);
    }

    private String getAdminAccessToken() {
        // IMPORTANT: bootstrap admin user is in "master" realm (unless you explicitly created it elsewhere).
        String tokenUrl = String.format(
                "%s/realms/%s/protocol/openid-connect/token",
                trimTrailingSlash(keycloakBaseUrl),
                keycloakAdminRealm
        );

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "password");
        form.add("client_id", "admin-cli");

        // admin-cli is public by default; only send secret if explicitly configured.
        if (keycloakAdminClientSecret != null && !keycloakAdminClientSecret.isBlank()) {
            form.add("client_secret", keycloakAdminClientSecret);
        }

        form.add("username", keycloakAdminUsername);
        form.add("password", keycloakAdminPassword);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> resp = restTemplate.exchange(tokenUrl, HttpMethod.POST, new HttpEntity<>(form, headers), Map.class);
        Object token = resp.getBody() != null ? resp.getBody().get("access_token") : null;
        if (token == null) {
            throw new IllegalStateException("Keycloak token response missing access_token (admin realm: " + keycloakAdminRealm + ")");
        }
        return token.toString();
    }

    private void assignClientRoleToUser(String userId, String roleName, String adminToken) {
        if (userId == null || userId.isBlank()) {
            return;
        }

        String kc = trimTrailingSlash(keycloakBaseUrl);
        // Najpierw pobierz ID klienta po clientId
        String clientsUrl = String.format("%s/admin/realms/%s/clients?clientId=%s", kc, realm, clientId);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

        ResponseEntity<Object[]> clientsResp = restTemplate.exchange(clientsUrl, HttpMethod.GET, new HttpEntity<>(headers), Object[].class);
        if (clientsResp.getBody() == null || clientsResp.getBody().length == 0) {
            return;
        }
        Map first = (Map) clientsResp.getBody()[0];
        String clientUuid = String.valueOf(first.get("id"));

        // Pobierz definicję roli klienta
        String roleUrl = String.format("%s/admin/realms/%s/clients/%s/roles/%s", kc, realm, clientUuid, roleName);
        ResponseEntity<Map> roleResp = restTemplate.exchange(roleUrl, HttpMethod.GET, new HttpEntity<>(headers), Map.class);
        Map role = roleResp.getBody();
        if (role == null) {
            return;
        }

        // Przypisz rolę
        String assignUrl = String.format("%s/admin/realms/%s/users/%s/role-mappings/clients/%s", kc, realm, userId, clientUuid);
        HttpHeaders h2 = new HttpHeaders();
        h2.setBearerAuth(adminToken);
        h2.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.exchange(assignUrl, HttpMethod.POST, new HttpEntity<>(new Object[]{role}, h2), Void.class);
    }

    private static String trimTrailingSlash(String url) {
        if (url == null) return "";
        return url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }
}
