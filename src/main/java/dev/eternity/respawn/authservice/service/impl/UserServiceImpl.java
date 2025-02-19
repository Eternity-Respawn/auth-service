package dev.eternity.respawn.authservice.service.impl;

import dev.eternity.respawn.authservice.dto.User.UserRegistrationRequestDto;
import dev.eternity.respawn.authservice.dto.User.UserRegistrationResponseDto;
import dev.eternity.respawn.authservice.exception.InvalidRoleException;
import dev.eternity.respawn.authservice.exception.UserExistsException;
import dev.eternity.respawn.authservice.mapper.UserMapper;
import dev.eternity.respawn.authservice.model.Role;
import dev.eternity.respawn.authservice.model.User;
import dev.eternity.respawn.authservice.repository.RoleRepository;
import dev.eternity.respawn.authservice.repository.UserRepository;
import dev.eternity.respawn.authservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private static final String REGISTRATION_EXCEPTION_MESSAGE_TEMPLATE
            = "User with email: %s is already exists";
    private static final String ADMIN_ROLE_EXCEPTION_TEMPLATE
            = "You can't register an account with type: ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserRegistrationResponseDto register(UserRegistrationRequestDto requestDto) {
        String email = requestDto.getEmail();
        Role.RoleName roleName = requestDto.getRole();

        validateRequest(email, roleName);

        Role role = roleRepository.findByRoleName(roleName);
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        User user = userMapper.toModel(requestDto);
        user.setPassword(encodedPassword);
        user.setRole(role);
        user.setRegistrationType(User.RegistrationType.FORM);

        return userMapper.toDto(userRepository.save(user));
    }

    private void validateRequest(String email, Role.RoleName roleName) {
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserExistsException(String.format(
                    REGISTRATION_EXCEPTION_MESSAGE_TEMPLATE, email
            ));
        }

        if (roleName.equals(Role.RoleName.ADMIN)) {
            throw new InvalidRoleException(ADMIN_ROLE_EXCEPTION_TEMPLATE);
        }
    }
}
