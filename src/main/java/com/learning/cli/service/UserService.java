package com.learning.cli.service;

import com.learning.cli.model.User;

import java.util.Optional;

public interface UserService {
    User addUser(User user);

    Optional<User> getUserByName(String username);
}
