package com.learning.cli.service.Impl;

import com.learning.cli.model.User;
import com.learning.cli.repository.UserRepository;
import com.learning.cli.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        log.info("User " + user.getUsername() + " successfully registered.");
        return registeredUser;
    }

}
