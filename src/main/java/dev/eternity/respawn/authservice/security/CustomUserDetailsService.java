package dev.eternity.respawn.authservice.security;

import dev.eternity.respawn.authservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private static final String EXCEPTION_TEMPLATE = "Can't find user with email: %s";
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {
        return userRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException(
                        String.format(EXCEPTION_TEMPLATE, email))
        );
    }
}
