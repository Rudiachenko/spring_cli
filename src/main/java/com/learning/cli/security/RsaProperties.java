package com.learning.cli.security;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@ConfigurationProperties(prefix = "rsa")
@Slf4j
@Data
public class RsaProperties {
    private String modulus;
    private String publicKey;
    private String privateKey;
    private RSAPublicKey publicKeyRsa;
    private RSAPrivateKey privateKeyRsa;

    @PostConstruct
    public void init() {
        try {
            publicKeyRsa = loadPublicKey(publicKey);
        } catch (Exception e) {
            log.error("It's impossible to load RSA public key", e);
        }

        try {
            privateKeyRsa = loadPrivateKey(privateKey);
        } catch (Exception e) {
            log.error("It's impossible to load RSA private key", e);
        }
    }

    private RSAPublicKey loadPublicKey(String publicKeyString) throws GeneralSecurityException {
        byte[] data = Base64.getDecoder().decode(publicKeyString);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(data);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPublicKey) factory.generatePublic(spec);
    }

    private RSAPrivateKey loadPrivateKey(String privateKeyString) throws GeneralSecurityException {
        byte[] data = Base64.getDecoder().decode(privateKeyString);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(data);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return (RSAPrivateKey) factory.generatePrivate(spec);
    }
}
