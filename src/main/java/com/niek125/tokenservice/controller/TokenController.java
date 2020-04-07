package com.niek125.tokenservice.controller;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.niek125.tokenservice.events.UserLoggedInEvent;
import com.niek125.tokenservice.kafka.KafkaDispatcher;
import com.niek125.tokenservice.models.Permission;
import com.niek125.tokenservice.models.User;
import com.niek125.tokenservice.token.TokenBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@RestController
@RequestMapping("/token")
public class TokenController {
    private final String roleManagement = "https://role-management-service/role/getroles/";

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
    public String getNewToken(@RequestHeader("gtoken") String gtoken) throws IOException, FirebaseAuthException, FirebaseAuthException {
        logger.info("getting firebase token");
        final FirebaseToken decodedToken = firebaseAuth.verifyIdToken(gtoken);
        final User user = new User(decodedToken.getUid(), decodedToken.getPicture(), decodedToken.getName(), decodedToken.getEmail());
        logger.info("sending UserLoggedInEvent");
        kafkaDispatcher.dispatch("user", new UserLoggedInEvent(user));
        logger.info("getting roles");
        final Permission[] permissions = restTemplate.getForObject(roleManagement + decodedToken.getUid(), Permission[].class);
        logger.info("generating token");
        final String token = tokenBuilder.getNewToken(decodedToken.getUid(), decodedToken.getName(), decodedToken.getPicture(), permissions);
        logger.info("returning token");
        return token;
    }

    @GetMapping("refresh")
    public String getRefreshedToken(@RequestHeader("dToken") String dtoken) {
        return null;
    }
}
