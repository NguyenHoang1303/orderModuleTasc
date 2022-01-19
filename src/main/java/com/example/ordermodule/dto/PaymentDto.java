package com.example.ordermodule.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PaymentDto {

    private Long orderId;
    private Long userId;
    private String paymentStatus;
    private String message;
    private String device_token;
}
