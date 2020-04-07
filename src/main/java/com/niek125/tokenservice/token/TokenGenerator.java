package com.niek125.tokenservice.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.niek125.tokenservice.models.Permission;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class TokenGenerator implements TokenBuilder {
    private final ObjectMapper objectMapper;
    private final Algorithm algorithm;

    public TokenGenerator(ObjectMapper objectMapper, Algorithm algorithm) {
        this.objectMapper = objectMapper;
        this.algorithm = algorithm;
    }

    @Override
    @SneakyThrows
    public String getNewToken(String uid, String userName, String pfp, Permission[] permissions) {
        return JWT.create()
                .withIssuer("data-editor-token-service")
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                .withClaim("uid", uid)
                .withClaim("unm", userName)
                .withClaim("pfp", pfp)
                .withClaim("pms", objectMapper.writeValueAsString(permissions))
                .sign(algorithm);
    }

}
