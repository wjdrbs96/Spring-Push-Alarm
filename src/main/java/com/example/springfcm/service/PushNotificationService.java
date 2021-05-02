package com.example.springfcm.service;

import com.example.springfcm.dto.PushNotificationRequest;

/**
 * created by jg 2021/05/02
 */

public interface PushNotificationService {

    void registerToken(Long userId, String token);

    String getToken(Long userId);

    void sendPushNotification(PushNotificationRequest notificationRequest);
}
