package com.niek125.tokenservice.config;

import com.auth0.jwt.algorithms.Algorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

import static com.niek125.tokenservice.utils.PemUtils.readPrivateKeyFromFile;

@Configuration
public class TokenConfig {
    @Value("${dataEditor.privateKey}")
    private String privateKey;

    @Bean
    public Algorithm algorithm() throws IOException {
        java.security.Security.addProvider(
                new org.bouncycastle.jce.provider.BouncyCastleProvider()
        );

        return Algorithm.RSA512(null, readPrivateKeyFromFile(privateKey, "RSA"));
    }
}
