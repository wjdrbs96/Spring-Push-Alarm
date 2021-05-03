package com.example.springfcm.service;

import com.example.springfcm.dto.PushNotificationRequest;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * created by jg 2021/05/02
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class FirebasePushNotificationService implements PushNotificationService {

    @Value("${fcm.account.path}")
    private String accountPath;

    private final RedisTemplate<String, String> redisTemplate;

    @PostConstruct
    public void init() {
        try (InputStream serviceAccount = Files.newInputStream(Paths.get(System.getProperty("user.dir") + accountPath))) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                log.info("Firebase Cloud Messaging 서비스를 성공적으로 초기화하였습니다.");
            }


        } catch (IOException e) {
            log.error("cannot initial firebase " + e.getMessage());
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
        final ListOperations<String, String> stringListOperations = redisTemplate.opsForList();

//        String token = "cYm9R_j7ReuSFz6Z2xZT6r:APA91bFgFquTCqTFXFYDK69kNrS_dRTCxdIPw7frEyG8IfcQ9AyovzS8sz-dhjCJoQTwXKI0G_IvcMy4Ae80Woou5SyeMyJ8faJd2ifPR-JsuSJofMIduyfoEHUcOsLarOTOnR162PFI";
//        String token1 = "ep3BfifTSjuBtVR-QxipaQ:APA91bGCkxiiqERcuRHEgXr5P-QF711W0LxfCpwzW8N9dZUOnY30MNkgawAI9lirLflubWs3n224Osn4spauPsgPkifqsfcGhAgL8VKRTz91dewL4wCV0dZnAVBRf71SIMxI9OygFWAr";

//        if (token == null) {
//            return;
//        }

        List<String> tokens = stringListOperations.range("token", 0, 3);

        List<Message> messages = tokens.stream().map(token -> Message.builder()
                .putData("title", pushNotificationRequest.getTitle())
                .putData("message", pushNotificationRequest.getMessage())
                .setToken(token)
                .build()).collect(Collectors.toList());

//
//
//        Message pushMessage1 = writePushMessage(pushNotificationRequest, token);
//        Message pushMessage2 = writePushMessage(pushNotificationRequest, token1);
//        List<Message> list = new ArrayList<>();
//        list.add(pushMessage1);
//        list.add(pushMessage2);
//        writePushMessage(pushNotificationRequest, token1);
//        try {
//            FirebaseMessaging.getInstance().sendAll(list);
//        } catch (FirebaseMessagingException e) {
//            log.error("Error");
//        }

        // 여러명 한테 보내기
        BatchResponse response;
        try {
            response = FirebaseMessaging.getInstance().sendAll(messages);
            log.info("Sent message: " + response);
        } catch (FirebaseMessagingException e) {
            log.error("cannot send to member push message. error info : {}", e.getMessage());
        }
    }

    // 한명한테 보내기
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
