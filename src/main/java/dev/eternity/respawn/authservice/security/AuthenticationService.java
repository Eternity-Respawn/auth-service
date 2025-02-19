package dev.eternity.respawn.authservice.security;

import dev.eternity.respawn.authservice.dto.user.UserLoginRequestDto;
import dev.eternity.respawn.authservice.dto.user.UserResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;

    public UserResponseDto authenticate(UserLoginRequestDto requestDto) {
        Authentication authenticate = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        requestDto.getEmail(), requestDto.getPassword()
                )
        );

        String token = jwtUtil.generateToken(requestDto.getEmail());
        return new UserResponseDto().setAccessToken(token);
    }
}
