package com.example.ordermodule.dto;

import com.example.ordermodule.entity.Order;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {
    private Long orderId;
    private Long userId;
    private double totalPrice;
    private int checkOut;
    private int status;
    private String device_token;

    public OrderDto(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.totalPrice = order.getTotalPrice();
        this.checkOut = order.getCheckOut();
        this.device_token = order.getDevice_token();
    }
}
