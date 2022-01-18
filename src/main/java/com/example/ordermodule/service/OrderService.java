package com.example.ordermodule.service;

import com.example.ordermodule.controller.CartController;
import com.example.ordermodule.dto.OrderDto;
import com.example.ordermodule.dto.PaymentDto;
import com.example.ordermodule.entity.Cart;
import com.example.ordermodule.entity.Order;
import com.example.ordermodule.enums.Status;
import com.example.ordermodule.repo.OrderRepo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDate;

import static com.example.ordermodule.queue.Config.DIRECT_EXCHANGE;
import static com.example.ordermodule.queue.Config.DIRECT_ROUTING_KEY_ORDER;

@Service
public class OrderService {

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    CartController cartController;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Order create(@RequestBody Order order) {
        double totalPrice = 0;
        for (Cart cart : CartController.cartHashMap.values()) {
            totalPrice += cart.getUnitPrice() * cart.getQuantity();
        }
        order.setTotalPrice(totalPrice);
        order.setCreatedAt(LocalDate.now());
        order.setCheckout(Status.Checkout.UNPAID.name());
        order.setStatus(Status.Order.PENDING.name());
        Order orderSave = new Order();
        try {
            orderSave = orderRepo.save(order);
            System.out.println(orderSave);
            System.out.println(new OrderDto(orderSave));
            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_ROUTING_KEY_ORDER, new OrderDto(orderSave));
            cartController.clear();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return orderSave;
    }

    public Page<Order> getAll(int page, int pageSize) {
        if (page <= 0) {
            page = 1;
        }
        if (pageSize < 0) {
            page = 6;
        }
        return orderRepo.findAll(PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "id"));
    }

    public Order handlerOrder(PaymentDto paymentDto) {

        if (!validationPaymentDto(paymentDto)) return null;

        Order orderExist = orderRepo.findById(paymentDto.getOrderId()).orElse(null);
        if (orderExist == null) {
            System.out.println("Order not found.");
            return null;
        }

        if (paymentDto.getCheckout().equals(Status.Checkout.PAID.name())) {
            System.out.println("Order đã thanh toán thành công");
            orderExist.setCheckout(Status.Checkout.PAID.name());
            return orderRepo.save(orderExist);
        }
        if (paymentDto.getCheckout().equals(Status.Checkout.UNPAID.name())) {
            System.out.println("Order thanh toan loi");
            return orderExist;
        }

        try {
            System.out.println("Order đã thanh toán thành công");
            return orderRepo.save(orderExist);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private boolean validationPaymentDto(PaymentDto paymentDto) {
        return paymentDto.getCheckout() != null
            && paymentDto.getUserId() != null
            && paymentDto.getOrderId() != null;
    }


}
