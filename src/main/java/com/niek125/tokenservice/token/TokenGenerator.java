package com.niek125.tokenservice.token;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.jose4j.jws.AlgorithmIdentifiers;
import org.jose4j.jws.JsonWebSignature;
import org.jose4j.jwt.JwtClaims;
import org.springframework.stereotype.Component;

import java.security.Key;

@Component
@AllArgsConstructor
public class TokenGenerator implements TokenBuilder {
    private final Key key;

    private JwtClaims getDefaultClaims() {
        final JwtClaims claims = new JwtClaims();
        claims.setIssuer("data-editor-token-service");
        claims.setGeneratedJwtId();
        claims.setIssuedAtToNow();
        claims.setExpirationTimeMinutesInTheFuture(60);
        return claims;
    }

    private void setExtraClaims(JwtClaims claims, String uid, String username, String pfp, String permissions) {
        claims.setClaim("uid", uid);
        claims.setClaim("unm", username);
        claims.setClaim("pfp", pfp);
        claims.setClaim("pms", permissions);
    }

    @Override
    @SneakyThrows
    public String getNewToken(String uid, String username, String pfp, String permissions) {
        final JwtClaims claims = getDefaultClaims();
        setExtraClaims(claims, uid, username, pfp, permissions);

        JsonWebSignature signature = new JsonWebSignature();
        signature.setPayload(claims.toJson());
        signature.setKey(key);
        signature.setAlgorithmHeaderValue(AlgorithmIdentifiers.RSA_USING_SHA256);

        return signature.getCompactSerialization();
    }

}
