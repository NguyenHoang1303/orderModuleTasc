package com.example.ordermodule.dto;

import com.example.ordermodule.entity.Order;
import lombok.*;

import java.util.HashMap;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {

    private Long orderId;
    private Long userId;
    private double totalPrice;
    private String checkout;
    private String status;
    private String device_token;
    private HashMap<Long, Integer> productAndQuantity;



    public OrderDto(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.totalPrice = order.getTotalPrice();
        this.checkout = order.getPaymentStatus();
        this.status = order.getOrderStatus();
        this.device_token = order.getDevice_token();
    }
}
