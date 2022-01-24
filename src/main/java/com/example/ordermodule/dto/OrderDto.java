package com.example.ordermodule.dto;

import com.example.ordermodule.entity.Order;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderDto {

    private Long orderId;
    private Long userId;
    private Set<OrderDetailDto> orderDetails = new HashSet<>();
    private BigDecimal totalPrice;
    private String paymentStatus;
    private String inventoryStatus;
    private String orderStatus;
    private String device_token;
    private String message;

    public OrderDto(Order order) {
        this.orderId = order.getId();
        this.userId = order.getUserId();
        this.totalPrice = order.getTotalPrice();
        this.paymentStatus = order.getPaymentStatus();
        this.inventoryStatus = order.getInventoryStatus();
        this.orderStatus = order.getOrderStatus();
        order.getOrderDetails().forEach(orderDetail -> {
            this.orderDetails.add(new OrderDetailDto(orderDetail));
        });
    }

    public boolean validationPayment(){
        return this.totalPrice.compareTo(BigDecimal.valueOf(0)) > 0
                && this.orderId != null && this.userId != null && this.paymentStatus != null;
    }

    public boolean validationInventory(){
        return this.orderId != null && this.orderStatus != null && this.inventoryStatus != null;
    }

}
