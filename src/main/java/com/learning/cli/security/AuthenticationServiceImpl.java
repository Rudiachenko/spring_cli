package com.learning.cli.security;

import com.learning.cli.model.Role;
import com.learning.cli.model.User;
import com.learning.cli.repository.RoleRepository;
import com.learning.cli.repository.UserRepository;
import com.learning.cli.service.UserService;
import io.micrometer.common.util.StringUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;

@Log4j2
@Service
public class AuthenticationServiceImpl implements AuthenticationService {
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationServiceImpl(RoleRepository roleRepository, UserService userService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(String username, String password, String repeatPassword) {
        validateParameters(username, password, repeatPassword);
        User user = createUser(username, password);
        return userService.addUser(user);
    }

    private User createUser(String username, String password) {
        Role userRole = roleRepository.findRoleByRoleName(Role.RoleName.USER);
        String encodedPassword = passwordEncoder.encode(password);
        return new User(username, encodedPassword, Set.of(userRole));
    }

    private void validateParameters(String username, String password, String repeatPassword) {
        checkUsername(username);
        checkPassword(password, repeatPassword);
    }

    private void checkUsername(String username) {
        if (StringUtils.isEmpty(username)) {
            log.warn("Username can't be null or empty");
            throw new IllegalArgumentException("Username can't be null or empty.");
        } else if (userRepository.findByUsername(username).isPresent()) {
            log.warn("User {} is already in use.", username);
            throw new IllegalArgumentException("User is already in use");
        }
    }

    private void checkPassword(String password, String repeatPassword) {
        if (!Objects.equals(password, repeatPassword)) {
            throw new IllegalArgumentException("'Password' and 'Repeat password' are not equals.");
        }
    }
}
