package com.example.ordermodule.service;

import com.example.ordermodule.dto.PaymentDto;
import com.example.ordermodule.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrderService {
    Order create(@RequestBody Order order);

    Page<Order> getAll(int page, int pageSize);

    @Transactional
    void handlerOrderPayment(PaymentDto paymentDto);
}
