package com.example.ordermodule.controller;


import com.example.ordermodule.dto.OrderDto;
import com.example.ordermodule.dto.PaymentDto;
import com.example.ordermodule.entity.Cart;
import com.example.ordermodule.entity.Order;
import com.example.ordermodule.repo.OrderRepo;
import com.example.ordermodule.response.RESTPagination;
import com.example.ordermodule.response.RESTResponse;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

import static com.example.ordermodule.queue.Config.*;

@RestController
@RequestMapping("api/v1/order")
public class OrderController {

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    CartController cartController;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @RequestMapping(method = RequestMethod.POST, path = "create")
    public ResponseEntity create(@RequestBody Order order) {
        double totalPrice = 0;
        for (Cart cart : CartController.cartHashMap.values()) {
            totalPrice += cart.getUnitPrice() * cart.getQuantity();
        }
        order.setTotalPrice(totalPrice);
        order.setCreatedAt(LocalDate.now());
        Order orderSave = new Order();
        try {
            orderSave = orderRepo.save(order);
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, new OrderDto(orderSave));
            cartController.clear();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

        return new ResponseEntity<>(
                new RESTResponse.Success()
                        .addData(orderSave)
                        .build(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "")
    public ResponseEntity getAll(@RequestParam(name = "page", defaultValue = "1") int page,
                                 @RequestParam(name = "pageSize", defaultValue = "9") int pageSize

    ) {
        if (page <= 0 ){
            page = 1;
        }
        if (pageSize < 0){
            page = 9;
        }
        Page paging = orderRepo.findAll(PageRequest.of(page - 1, pageSize));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setPagination(new RESTPagination(paging.getNumber() + 1, paging.getSize(), paging.getTotalElements()))
                .addData(paging.getContent())
                .buildData(), HttpStatus.OK);
    }


    public Order handlerOrder(PaymentDto paymentDto) {
        Order orderExist = orderRepo.findById(paymentDto.getOrderId()).orElse(null);
        if (orderExist == null) {
            System.out.println("Order not found.");
            return null;
        }

        if (paymentDto.getCheckOut() == 1) {
            System.out.println("Order đã thanh toán thành công");
            orderExist.setCheckOut(1);
            return orderRepo.save(orderExist);
        }
        if (paymentDto.getCheckOut() == 0){
            System.out.println("Order thanh toan loi");
            return  orderExist;
        }

        try {
            System.out.println("Order đã thanh toán thành công");
            return orderRepo.save(orderExist);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }

    }

}
