package com.example.ordermodule.service;

import com.example.ordermodule.dto.OrderDto;
import com.example.ordermodule.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;

public interface OrderService {
    OrderDto create(@RequestBody Order order);

    Page<Order> getAll(int page, int pageSize);

    Order findById(Long orderId);

    Order save(Order orderExist);
}
