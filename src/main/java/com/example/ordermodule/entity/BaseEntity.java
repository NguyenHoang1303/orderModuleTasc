package com.example.ordermodule.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BaseEntity {

    private String isRemove;
    private LocalDate createdAt;
    private LocalDate updateAt;
    private LocalDate deleteAt;
}
