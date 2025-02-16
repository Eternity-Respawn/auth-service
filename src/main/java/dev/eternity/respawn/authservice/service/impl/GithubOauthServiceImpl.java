package dev.eternity.respawn.authservice.service.impl;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import dev.eternity.respawn.authservice.dto.UserResponseDto;
import dev.eternity.respawn.authservice.model.User;
import dev.eternity.respawn.authservice.repository.UserRepository;
import dev.eternity.respawn.authservice.security.JwtUtil;
import dev.eternity.respawn.authservice.service.OauthService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GithubOauthServiceImpl implements OauthService {
    private static final String GITHUB_AUTHORIZATION_SERVER_PATH
            = "https://github.com/login/oauth/authorize";
    private static final String GITHUB_EXCHANGE_TOKEN_PATH
            = "https://github.com/login/oauth/access_token";
    private static final String GITHUB_PROFILE_INFO_PATH = "https://api.github.com/user";
    private static final String BEARER_AUTH = "Bearer %s";

    @Value("${github.client.id}")
    private String clientId;
    @Value("${github.client.secret}")
    private String secret;

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
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

        String accessToken = jsonObject.get("access_token").toString();
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
        String headerValue = String.format(BEARER_AUTH, accessToken.replace("\"", ""));

        HttpRequest userProfileRequest = HttpRequest
                .newBuilder(URI.create(GITHUB_PROFILE_INFO_PATH))
                .GET()
                .header("Authorization", headerValue)
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> profileInfo = httpClient.send(
                userProfileRequest, HttpResponse.BodyHandlers.ofString()
        );

        JsonObject jsonObject = new Gson().fromJson(profileInfo.body(), JsonObject.class);

        return saveUser(jsonObject);
    }

    private User saveUser(JsonObject jsonObject) {
        User user = new User()
                .setFirstName(jsonObject.get("name").toString())
                .setLastName("")
                .setEmail(jsonObject.get("email").toString())
                .setPassword(UUID.randomUUID().toString())
                .setRegistrationType(User.RegistrationType.GITHUB);

        return userRepository.save(user);
    }
}
