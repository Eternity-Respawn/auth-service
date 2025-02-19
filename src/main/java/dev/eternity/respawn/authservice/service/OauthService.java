package dev.eternity.respawn.authservice.service;

import dev.eternity.respawn.authservice.dto.user.UserResponseDto;
import java.io.IOException;
import java.util.List;

public interface OauthService {
    String generateOauthRequest(
            String redirectUrl, List<String> scopes, String codeChallenge
    );

    UserResponseDto loginUserWithOauth(
            String code, String codeVerifier, String redirectUrl
    ) throws IOException, InterruptedException;
}
