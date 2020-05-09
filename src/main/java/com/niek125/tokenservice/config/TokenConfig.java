package com.niek125.tokenservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.security.Key;

import static com.niek125.tokenservice.utils.PemUtils.readPrivateKeyFromFile;

@Configuration
public class TokenConfig {
    @Value("${dataEditor.privateKey}")
    private String privateKey;

    @Bean
    public Key algorithm() throws IOException {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        return readPrivateKeyFromFile(privateKey, "RSA");
    }
}
