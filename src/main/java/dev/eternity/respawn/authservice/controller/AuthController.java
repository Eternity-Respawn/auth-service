package dev.eternity.respawn.authservice.controller;

import dev.eternity.respawn.authservice.dto.User.UserLoginRequestDto;
import dev.eternity.respawn.authservice.dto.User.UserRegistrationRequestDto;
import dev.eternity.respawn.authservice.dto.User.UserRegistrationResponseDto;
import dev.eternity.respawn.authservice.dto.User.UserResponseDto;
import dev.eternity.respawn.authservice.security.AuthenticationService;
import dev.eternity.respawn.authservice.service.OauthService;
import dev.eternity.respawn.authservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private static final List<String> GITHUB_SCOPES = List.of("read:user", "user:email");
    private static final String GITHUB_REDIRECT_URL
            = "http://localhost:8080/api/auth/oauth2/code/github";
    private final OauthService githubOauthServiceImpl;
    private final AuthenticationService authenticationService;
    private final UserService userService;

    @PostMapping("/register")
    public UserRegistrationResponseDto register(
            @RequestBody @Valid UserRegistrationRequestDto requestDto
    ) {
        return userService.register(requestDto);
    }

    @PostMapping("/login")
    public UserResponseDto login(@RequestBody @Valid UserLoginRequestDto requestDto) {
        return authenticationService.authenticate(requestDto);
    }

    @GetMapping("/github")
    public RedirectView redirectOnGithubAuthServer() {
        String url = githubOauthServiceImpl.generateOauthRequest(
                GITHUB_REDIRECT_URL, GITHUB_SCOPES, null
        );
        return new RedirectView(url);
    }

    @GetMapping("/oauth2/code/github")
    public UserResponseDto loginWithGithub(@RequestParam("code") String code)
            throws IOException, InterruptedException {
        return githubOauthServiceImpl.loginUserWithOauth(
                code, null, GITHUB_REDIRECT_URL
        );
    }
}
