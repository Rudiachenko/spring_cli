package com.learning.cli.controller;

import com.learning.cli.model.CustomUserDetails;
import com.learning.cli.model.User;
import com.learning.cli.security.AuthenticationService;
import com.learning.cli.security.service.TokenService;
import com.learning.cli.service.Impl.CustomUserDetailsService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@Log4j2
@RestController
public class AuthController {
    private static final String BEARER_PREFIX = "Bearer ";
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
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(request.username, request.password);
        try {
            Authentication authenticate = authManager.authenticate(authenticationToken);
            CustomUserDetails userDetails = (CustomUserDetails) authenticate.getPrincipal();
            String generatedAccessToken = tokenService.generateAccessToken(userDetails);
            String generatedRefreshToken = tokenService.generateRefreshToken(userDetails);
            String successMessage = "User with username = " + request.username + " successfully logged in!";
            log.info(successMessage);
            return ResponseEntity.ok(new LoginResponse(successMessage, generatedAccessToken, generatedRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/token/refresh")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestHeader("Authorization") String headerAuth) {
        if (headerAuth == null || !headerAuth.startsWith(BEARER_PREFIX)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            String refreshToken = headerAuth.substring(BEARER_PREFIX.length());
            String username = tokenService.parseToken(refreshToken);
            CustomUserDetails user = (CustomUserDetails) userDetailsService.loadUserByUsername(username);
            RefreshTokenResponse refreshTokenResponse = new RefreshTokenResponse(tokenService.generateAccessToken(user),
                    tokenService.generateRefreshToken(user));
            log.info("Token for user '{}' successfully refreshed", username);
            return ResponseEntity.ok(refreshTokenResponse);
        } catch (Exception e) {
            log.error("Failed to refresh token", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
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
