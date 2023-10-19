package com.learning.cli.controller;

import com.learning.cli.model.CustomUserDetails;
import com.learning.cli.model.User;
import com.learning.cli.security.AuthenticationService;
import com.learning.cli.security.TokenService;
import com.learning.cli.service.Impl.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class AuthController {
    private final TokenService tokenService;
    private final AuthenticationManager authManager;
    private final CustomUserDetailsService userDetailsService;
    private final AuthenticationService authenticationService;

    @Autowired
    public AuthController(TokenService tokenService, AuthenticationManager authManager,
                          CustomUserDetailsService userDetailsService,
                          AuthenticationService authenticationService) {
        super();
        this.tokenService = tokenService;
        this.authManager = authManager;
        this.userDetailsService = userDetailsService;
        this.authenticationService = authenticationService;
    }

    @PostMapping("/registration")
    public ResponseEntity<User> registration(@RequestBody RegisterRequest request) {
        User registeredUser = authenticationService.register(request.username, request.password, request.repeatPassword);
        return ResponseEntity.ok(registeredUser);
    }

    @PostMapping("/login")
    public LoginResponse login(@RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.username, request.password);
        authManager.authenticate(authenticationToken);

        CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(request.username);

        return new LoginResponse("User with username = " + request.username + " successfully logined!",
                tokenService.generateAccessToken(user), tokenService.generateRefreshToken(user));
    }

    @GetMapping("/token/refresh")
    public RefreshTokenResponse refreshToken(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");
        String refreshToken = headerAuth.substring(7);

        String username = tokenService.parseToken(refreshToken);
        CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(username);

        return new RefreshTokenResponse(tokenService.generateAccessToken(user),
                tokenService.generateRefreshToken(user));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    record RegisterRequest(String username, String password, String repeatPassword) {
    }

    record LoginRequest(String username, String password) {
    }

    record LoginResponse(String message, String accessJwtToken, String refreshJwtToken) {
    }

    record RefreshTokenResponse(String accessJwtToken, String refreshJwtToken) {
    }
}
