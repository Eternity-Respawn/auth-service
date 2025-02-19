package dev.eternity.respawn.authservice.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import dev.eternity.respawn.authservice.dto.github.GithubEmailDto;
import dev.eternity.respawn.authservice.dto.github.GithubResponseDto;
import dev.eternity.respawn.authservice.dto.user.UserResponseDto;
import dev.eternity.respawn.authservice.model.Role;
import dev.eternity.respawn.authservice.model.User;
import dev.eternity.respawn.authservice.repository.RoleRepository;
import dev.eternity.respawn.authservice.repository.UserRepository;
import dev.eternity.respawn.authservice.security.JwtUtil;
import dev.eternity.respawn.authservice.service.OauthService;
import java.io.IOException;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class GithubOauthServiceImpl implements OauthService {
    private static final String GITHUB_AUTHORIZATION_SERVER_PATH
            = "https://github.com/login/oauth/authorize";
    private static final String GITHUB_EXCHANGE_TOKEN_PATH
            = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_PROFILE_INFO_PATH = "https://api.github.com/user";
    private static final String GITHUB_EMAIL_PATH = "https://api.github.com/user/emails";
    private static final String BEARER_AUTH = "Bearer %s";

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String secret;

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtUtil jwtUtil;
    private final Gson gson;
    private final PasswordEncoder passwordEncoder;
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Override
    public String generateOauthRequest(
            String redirectUrl,
            List<String> scopes,
            String codeChallenge
    ) {
        String scope = String.join(" ", scopes);

        return UriComponentsBuilder.fromUriString(GITHUB_AUTHORIZATION_SERVER_PATH)
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUrl)
                .queryParam("scope", scope)
                .toUriString();
    }

    @Override
    public UserResponseDto loginUserWithOauth(
            String code,
            String codeVerifier,
            String redirectUrl
    ) throws IOException, InterruptedException {
        String requestBody = generateRequestBody(code, redirectUrl);

        HttpRequest request = HttpRequest.newBuilder(URI.create(GITHUB_EXCHANGE_TOKEN_PATH))
                .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> exchangeCodeResponse = httpClient.send(
                request, HttpResponse.BodyHandlers.ofString()
        );

        JsonObject jsonObject = new Gson()
                .fromJson(exchangeCodeResponse.body(), JsonObject.class);

        String accessToken = jsonObject.get("access_token").getAsString();
        User user = getProfileDetails(accessToken);
        String token = jwtUtil.generateToken(user.getEmail());

        return new UserResponseDto().setAccessToken(token);
    }

    private String generateRequestBody(String code, String redirectUrl) {
        JsonObject body = new JsonObject();
        body.addProperty("client_id", clientId);
        body.addProperty("client_secret", secret);
        body.addProperty("code", code);
        body.addProperty("redirect_uri", redirectUrl);

        return body.toString();
    }

    private User getProfileDetails(String accessToken)
            throws IOException, InterruptedException {
        String headerValue = String.format(BEARER_AUTH, accessToken);

        HttpRequest userProfileRequest = generateUserProfileGetRequest(
                GITHUB_PROFILE_INFO_PATH, headerValue
        );

        HttpResponse<String> profileInfo = httpClient.send(
                userProfileRequest, HttpResponse.BodyHandlers.ofString()
        );

        GithubResponseDto userProfile = gson.fromJson(
                profileInfo.body(), GithubResponseDto.class
        );

        return getOrRegisterUser(userProfile, headerValue);
    }

    private User getOrRegisterUser(GithubResponseDto userProfile, String headerValue)
            throws IOException, InterruptedException {
        String email = userProfile.getEmail();

        if (email == null) {
            HttpRequest userEmailRequest = generateUserProfileGetRequest(
                    GITHUB_EMAIL_PATH, headerValue
            );

            HttpResponse<String> userEmailResponse = httpClient.send(
                    userEmailRequest, HttpResponse.BodyHandlers.ofString()
            );

            Type type = new TypeToken<ArrayList<GithubEmailDto>>(){}.getType();
            List<GithubEmailDto> userEmails = gson.fromJson(userEmailResponse.body(), type);

            email = findPrimaryEmail(userEmails);
        }

        return resolveUser(userProfile.getName(), email);
    }

    private HttpRequest generateUserProfileGetRequest(String path, String headerValue) {
        return HttpRequest.newBuilder(URI.create(path))
                .GET()
                .header("Authorization", headerValue)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();
    }

    private String findPrimaryEmail(List<GithubEmailDto> userEmails) {
        for (GithubEmailDto email: userEmails) {
            if (email.isPrimary()) {
                return email.getEmail();
            }
        }

        return "";
    }

    private User resolveUser(String name, String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            return userOptional.get();
        }

        Role role = roleRepository.findByRoleName(Role.RoleName.USER);

        User user = new User()
                .setFirstName(name)
                .setLastName("")
                .setEmail(email)
                .setPassword(passwordEncoder.encode(UUID.randomUUID().toString()))
                .setRegistrationType(User.RegistrationType.GITHUB)
                .setRole(role);

        return userRepository.save(user);
    }
}
