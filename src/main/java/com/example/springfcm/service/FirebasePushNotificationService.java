package com.example.springfcm.service;

import com.example.springfcm.dto.PushNotificationRequest;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.WebpushConfig;
import com.google.firebase.messaging.WebpushNotification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * created by jg 2021/05/02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FirebasePushNotificationService implements PushNotificationService {

    @Value("${fcm.account.path}")
    private String accountPath;

    @PostConstruct
    public void init() {
        try (InputStream serviceAccount = Files.newInputStream(Paths.get(accountPath))) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase Cloud Messaging 서비스를 성공적으로 초기화하였습니다.");
            }

        } catch (IOException e) {

        }
    }

    @Override
    public void registerToken(Long userId, String token) {

    }

    @Override
    public String getToken(Long userId) {
        return null;
    }

    @Override
    public void sendPushNotification(PushNotificationRequest pushNotificationRequest) {
        // 임시 테스트
        String token = "cYm9R_j7ReuSFz6Z2xZT6r:APA91bFgFquTCqTFXFYDK69kNrS_dRTCxdIPw7frEyG8IfcQ9AyovzS8sz-dhjCJoQTwXKI0G_IvcMy4Ae80Woou5SyeMyJ8faJd2ifPR-JsuSJofMIduyfoEHUcOsLarOTOnR162PFI";

        if (token == null) {
            return;
        }

        Message pushMessage = writePushMessage(pushNotificationRequest, token);
        FirebaseMessaging.getInstance().sendAsync(pushMessage);
    }

    private Message writePushMessage(PushNotificationRequest pushNotificationRequest, String token) {
        return Message.builder()
                .setWebpushConfig(WebpushConfig.builder()
                        .setNotification(WebpushNotification.builder()
                                .setTitle(pushNotificationRequest.getTitle())
                                .setBody(pushNotificationRequest.getMessage())
                                .build())
                        .build())
                .setToken(token)
                .build();
    }
}