package com.example.ordermodule.entity;


import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Cart {

    private Long productId;
    private String name;
    private int quantity;
    private String thumbnail;
    private BigDecimal unitPrice;

}
