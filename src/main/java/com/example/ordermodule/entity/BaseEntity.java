package com.example.ordermodule.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
public abstract class BaseEntity implements Serializable {

    public LocalDate createdAt;
    public LocalDate updateAt;
    public LocalDate deleteAt;
}
