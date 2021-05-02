package com.example.springfcm.dto;

import lombok.Builder;
import lombok.Getter;

/**
 * created by jg 2021/05/02
 */
@Getter
@Builder
public class PushNotificationRequest {
    private final String title;
    private final String message;

    public static PushNotificationRequest create(String title, String message) {
        return PushNotificationRequest.builder()
                .title(title)
                .message(message)
                .build();
    }
}
