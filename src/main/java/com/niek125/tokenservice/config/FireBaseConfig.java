package com.niek125.tokenservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class FireBaseConfig {

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("D:\\Semester3\\Software\\BigIdea\\Project\\backend\\TokenService\\src\\main\\resources\\dataeditor-firebase-adminsdk-fhb0o-c95d73760a.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://dataeditor.firebaseio.com")
                .build();

        FirebaseApp app = FirebaseApp.initializeApp(options);

        return FirebaseAuth.getInstance(app);
    }
}
