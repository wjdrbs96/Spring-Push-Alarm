package com.example.springfcm.controller;

import com.example.springfcm.dto.PushNotificationRequest;
import com.example.springfcm.service.FirebasePushNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * created by jg 2021/05/02
 */
@RequiredArgsConstructor
@RestController
public class TestController {

    private final FirebasePushNotificationService firebasePushNotificationService;

    @GetMapping("/")
    public String test() {
        PushNotificationRequest request = PushNotificationRequest.create("제목", "메세지");
        firebasePushNotificationService.sendPushNotification(request);
        return "test";
    }
}
