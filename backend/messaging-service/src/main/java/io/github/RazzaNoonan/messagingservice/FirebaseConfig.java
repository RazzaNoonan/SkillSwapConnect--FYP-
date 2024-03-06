package io.github.RazzaNoonan.messagingservice;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Component
public class FirebaseConfig {

    @PostConstruct
    public void initializeFirebase() {
        try {
            FileInputStream serviceAccount =
                    new FileInputStream("C:\\FYPimoportantFiles\\finalproject-398be-firebase-adminsdk-696bz-2a263bdddf.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) { //<-- check for already initialized apps
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Consider proper logging and error handling here
        }
    }
}
