package com.learning.cli.security;

import com.learning.cli.model.User;

public interface AuthenticationService {
    User register(String username, String password, String repeatPassword);
}
