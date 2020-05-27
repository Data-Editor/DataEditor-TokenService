package com.niek125.tokenservice.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.niek125.tokenservice.events.UserLoggedInEvent;
import com.niek125.tokenservice.kafka.KafkaDispatcher;
import com.niek125.tokenservice.models.Permission;
import com.niek125.tokenservice.models.User;
import com.niek125.tokenservice.token.TokenBuilder;
import lombok.extern.log4j.Log4j2;
import org.jose4j.jwt.JwtClaims;
import org.jose4j.jwt.consumer.InvalidJwtException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Log4j2
@RestController
@RequestMapping("/token")
public class TokenController {
    private final String roleManagement;
    private final JwtConsumer jwtConsumer;
    private final RestTemplate restTemplate;
    private final TokenBuilder tokenBuilder;
    private final FirebaseAuth firebaseAuth;
    private final KafkaDispatcher kafkaDispatcher;

    public TokenController(JwtConsumer jwtConsumer, RestTemplate restTemplate, TokenBuilder generator, FirebaseAuth firebaseAuth, KafkaDispatcher kafkaDispatcher) {
        this.jwtConsumer = jwtConsumer;
        this.restTemplate = restTemplate;
        this.tokenBuilder = generator;
        this.firebaseAuth = firebaseAuth;
        this.kafkaDispatcher = kafkaDispatcher;
        this.roleManagement = "http://role-service/role?userId=";
    }

    private String buildToken(String uid, String username, String pfp) {
        log.info("getting permissions");
        final Permission[] permissions = restTemplate.getForObject(roleManagement + uid, Permission[].class);
        log.info("generating token");
        return tokenBuilder.getNewToken(uid, username, pfp, permissions);
    }

    @GetMapping("/new")
    public ResponseEntity<String> getNewToken(@RequestHeader("firebaseToken") String firebaseToken) {
        try {
            log.info("getting firebase token");
            final FirebaseToken decodedToken = firebaseAuth.verifyIdToken(firebaseToken);
            final User user = new User(decodedToken.getUid(), decodedToken.getPicture(), decodedToken.getName(), decodedToken.getEmail());
            log.info("sending UserLoggedInEvent");
            kafkaDispatcher.dispatch("user", new UserLoggedInEvent(user));
            final String token = buildToken(decodedToken.getUid(), decodedToken.getName(), decodedToken.getPicture());
            log.info("returning token");
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (FirebaseAuthException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/refresh")
    public ResponseEntity<String> getRefreshedToken(@RequestHeader("Authorization") String dataEditorToken) {
        try {
            log.info("accessing old token");
            final JwtClaims claims = jwtConsumer.processToClaims(dataEditorToken.replace("Bearer ", ""));
            final String token = buildToken(
                    claims.getClaimValueAsString("uid"),
                    claims.getClaimValueAsString("unm"),
                    claims.getClaimValueAsString("pfp"));
            log.info("returning token");
            return new ResponseEntity<>(token, HttpStatus.OK);
        } catch (InvalidJwtException e) {
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
        }
    }
}
