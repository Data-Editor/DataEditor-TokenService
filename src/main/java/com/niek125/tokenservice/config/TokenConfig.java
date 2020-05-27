package com.niek125.tokenservice.config;

import org.jose4j.jwa.AlgorithmConstraints;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.Key;

import static com.niek125.tokenservice.utils.PemUtils.readPrivateKeyFromFile;
import static com.niek125.tokenservice.utils.PemUtils.readPublicKeyFromFile;

@Configuration
public class TokenConfig {
    @Value("${dataEditor.privateKey}")
    private String privateKey;

    @Bean
    public Key algorithm() {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        return readPrivateKeyFromFile(privateKey, "RSA");
    }

    @Value("${dataEditor.publicKey}")
    private String publicKey;

    @Bean
    public JwtConsumer jwtConsumer() {
        return new JwtConsumerBuilder()
                .setRequireJwtId()
                .setAllowedClockSkewInSeconds(60)
                .setExpectedIssuer("data-editor-token-service")
                .setVerificationKey(readPublicKeyFromFile(publicKey, "RSA"))
                .setJwsAlgorithmConstraints(AlgorithmConstraints.ConstraintType.WHITELIST, AlgorithmIdentifiers.RSA_USING_SHA256)
                .build();
    }
}
