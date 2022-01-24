package com.example.ordermodule.entity;


import lombok.*;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
@ToString
public class Order extends BaseEntity {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(mappedBy = "order",cascade = CascadeType.ALL)
    private Set<OrderDetail> orderDetails = new HashSet<>();

    private Long userId;
    private BigDecimal totalPrice;
    private String address;
    private String name;
    private String phone;
    private String email;
    private String paymentStatus;
    private String inventoryStatus;
    private String orderStatus;

}
