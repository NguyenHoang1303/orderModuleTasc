package com.example.ordermodule.controller;


import com.example.ordermodule.dto.OrderDto;
import com.example.ordermodule.dto.PaymentDto;
import com.example.ordermodule.entity.Cart;
import com.example.ordermodule.entity.Order;
import com.example.ordermodule.enums.Status;
import com.example.ordermodule.repo.OrderRepo;
import com.example.ordermodule.response.RESTPagination;
import com.example.ordermodule.response.RESTResponse;
import com.example.ordermodule.service.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.example.ordermodule.queue.Config.*;

@RestController
@RequestMapping("api/v1/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @Autowired
    CartController cartController;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(method = RequestMethod.POST, path = "create")
    public ResponseEntity create(@RequestBody Order order) {
        return new ResponseEntity<>(
                new RESTResponse.Success()
                        .addData(orderService.create(order))
                        .build(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity getAll(@RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "pageSize", defaultValue = "6") int pageSize
    ) {
        Page<Order> paging = orderService.getAll(page, pageSize);
        return new ResponseEntity<>(new RESTResponse.Success()
                .setPagination(new RESTPagination(paging.getNumber() + 1, paging.getSize(), paging.getTotalElements()))
                .addData(paging.getContent())
                .buildData(), HttpStatus.OK);
    }




}
