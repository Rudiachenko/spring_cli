package com.learning.cli.service.Impl;

import com.learning.cli.model.User;
import com.learning.cli.repository.UserRepository;
import com.learning.cli.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Log4j2
@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User addUser(User user) {
        User registeredUser = userRepository.insert(user);
        log.info("User '{}' successfully registered.", user.getUsername());
        return registeredUser;
    }

    @Override
    public Optional<User> getUserByName(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent()) {
            log.info("User '{}' successfully found.", user.get().getUsername());
        }
        return user;
    }
}
