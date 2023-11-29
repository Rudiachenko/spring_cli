package com.learning.cli.service.Impl;

import com.learning.cli.model.CustomUserDetails;
import com.learning.cli.model.User;
import com.learning.cli.repository.UserRepository;
import com.learning.cli.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class CustomUserDetailsService implements UserDetailsService {
    private final UserService userService;

    @Autowired
    public CustomUserDetailsService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.info("Getting user with username: " + username);
        Optional<User> user = getUser(username);
        if (user.isPresent()) {
            log.info(String.format("User with username: '%s' was got successfully", user.get().getUsername()));
            return new CustomUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException("Can't get user with username: " + username);
        }
    }

    private Optional<User> getUser(String username) {
        return userService.getUserByName(username);
    }
}
