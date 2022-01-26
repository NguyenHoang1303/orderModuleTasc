package com.example.ordermodule.controller;


import com.example.ordermodule.entity.Order;
import com.example.ordermodule.response.RESTPagination;
import com.example.ordermodule.response.RESTResponse;
import com.example.ordermodule.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

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

    @RequestMapping(method = RequestMethod.GET, path = "detail")
    public ResponseEntity getDetail(@RequestParam(name = "id") int id) {
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(orderService.findById((long) id))
                .buildData(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "user/detail")
    public ResponseEntity getOrderByUserId(@RequestParam(name = "userId") int userId) {
        userId= 451691;
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(orderService.findOrderByUserId(userId))
                .buildData(), HttpStatus.OK);
    }


}
