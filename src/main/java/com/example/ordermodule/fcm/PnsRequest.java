package com.example.ordermodule.fcm;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class PnsRequest {
    private String fcmToken;
    private String content;
    private String title;


}