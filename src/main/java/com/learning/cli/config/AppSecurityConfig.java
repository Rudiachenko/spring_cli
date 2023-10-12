package com.learning.cli.config;

import com.learning.cli.security.RsaProperties;
import com.learning.cli.security.TokenService;
import com.learning.cli.service.Impl.CustomUserDetailsService;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableGlobalMethodSecurity(prePostEnabled = true)
@EnableWebSecurity
@Configuration
@ComponentScan(basePackages = {
        "com.learning.cli.service",
        "com.learning.cli.security",
        "com.learning.cli.repository",
})
public class AppSecurityConfig {
    private final UserDetailsService customUserDetails;
    private final RsaProperties rsaProperties;

    @Autowired
    public AppSecurityConfig(CustomUserDetailsService userDetailsService, RsaProperties rsaProperties) {
        this.customUserDetails = userDetailsService;
        this.rsaProperties = rsaProperties;
    }

    @Bean
    public AuthenticationManager authManager() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(customUserDetails);
        authProvider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(authProvider);
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

//    @Bean
//    public RsaProperties rsaProperties() {
//       return new RsaProperties();
//    }

    @Bean
   public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder build = NimbusJwtDecoder.withPublicKey(rsaProperties.getPublicKeyRsa()).build();
        return build;
    }

    @Bean
    public TokenService tokenService() {
        return new TokenService(jwtEncoder());
    }

    @Bean
    public JwtEncoder jwtEncoder(){
        RSAKey jwk = new RSAKey.Builder(rsaProperties.getPublicKeyRsa())
                .privateKey(rsaProperties.getPrivateKeyRsa()).build();

        JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwkSource);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeRequests(auth -> auth
                        .mvcMatchers("/login").permitAll()
                        .mvcMatchers("/token/refresh").permitAll()
                        .mvcMatchers("/admin").hasRole("ADMIN")
                        .mvcMatchers("/user").hasRole("USER")
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .oauth2ResourceServer(httpSecurityOAuth2ResourceServerConfigurer -> httpSecurityOAuth2ResourceServerConfigurer.jwt())
                .build();
    }
}
