package com.niek125.tokenservice.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.niek125.tokenservice.events.UserLoggedInEvent;
import com.niek125.tokenservice.kafka.KafkaDispatcher;
import com.niek125.tokenservice.models.User;
import com.niek125.tokenservice.token.TokenBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/token")
public class TokenController {
    private final String roleManagement = "http://role-service/role/";

    private final Logger logger = LoggerFactory.getLogger(TokenController.class);
    private final RestTemplate restTemplate;
    private final TokenBuilder tokenBuilder;
    private final FirebaseAuth firebaseAuth;
    private final KafkaDispatcher kafkaDispatcher;

    public TokenController(RestTemplate restTemplate, TokenBuilder generator, FirebaseAuth firebaseAuth, KafkaDispatcher kafkaDispatcher) {
        this.restTemplate = restTemplate;
        this.tokenBuilder = generator;
        this.firebaseAuth = firebaseAuth;
        this.kafkaDispatcher = kafkaDispatcher;
    }

    @GetMapping("/new")
    public ResponseEntity<String> getNewToken(@RequestHeader("firebaseToken") String firebaseToken) {
        try {
            logger.info("getting firebase token");
            final FirebaseToken decodedToken = firebaseAuth.verifyIdToken(firebaseToken);
            final User user = new User(decodedToken.getUid(), decodedToken.getPicture(), decodedToken.getName(), decodedToken.getEmail());
            logger.info("sending UserLoggedInEvent");
            kafkaDispatcher.dispatch("user", new UserLoggedInEvent(user));
            logger.info("getting roles");
            final String permissions = restTemplate.getForObject(roleManagement + decodedToken.getUid(), String.class);
            logger.info("generating token");
            final String token = tokenBuilder.getNewToken(decodedToken.getUid(), decodedToken.getName(), decodedToken.getPicture(), permissions);
            logger.info("returning token");
            return new ResponseEntity<>(token, HttpStatus.OK);
        }
        catch (FirebaseAuthException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }

    }

    @GetMapping("refresh")
    public String getRefreshedToken(@RequestHeader("dToken") String dtoken) {
        return null;
    }
}
