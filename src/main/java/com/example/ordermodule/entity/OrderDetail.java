package com.example.ordermodule.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_details")
@Getter
@Setter
@ToString
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false,updatable = false)
    @JsonIgnore
    private Order order;

    @Column(name = "order_id")
    private Long orderId;

    private String productName;
    private int quantity;
    private BigDecimal unitPrice;

    public OrderDetail(Cart cart) {
        this.productId = cart.getProductId();
        this.productName = cart.getName();
        this.unitPrice = cart.getUnitPrice();
        this.quantity = cart.getQuantity();
    }
}
