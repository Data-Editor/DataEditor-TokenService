package com.niek125.tokenservice.token;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
@AllArgsConstructor
public class TokenGenerator implements TokenBuilder {
    private final Algorithm algorithm;

    @Override
    public String getNewToken(String uid, String username, String pfp, String permissions) {
        return JWT.create()
                .withIssuer("data-editor-token-service")
                .withJWTId(UUID.randomUUID().toString())
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + (60 * 60 * 1000)))
                .withClaim("uid", uid)
                .withClaim("unm", username)
                .withClaim("pfp", pfp)
                .withClaim("pms", permissions)
                .sign(algorithm);
    }

}
