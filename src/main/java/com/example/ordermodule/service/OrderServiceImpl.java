package com.example.ordermodule.service;

import com.example.ordermodule.controller.CartController;
import com.example.ordermodule.dto.OrderDto;
import com.example.ordermodule.entity.CartItem;
import com.example.ordermodule.entity.Order;
import com.example.ordermodule.entity.OrderDetail;
import com.example.ordermodule.enums.InventoryStatus;
import com.example.ordermodule.enums.Status;
import com.example.ordermodule.repo.OrderRepo;
import com.example.ordermodule.translate.TranslationService;
import common.event.OrderEvent;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static com.example.ordermodule.constant.KeyI18n.ORDER_NOT_PRODUCT;
import static com.example.ordermodule.queue.Config.DIRECT_EXCHANGE;
import static com.example.ordermodule.queue.Config.DIRECT_SHARE_ROUTING_KEY;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderRepo orderRepo;

    @Autowired
    CartController cartController;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    TranslationService translationService;

    @Override
    @Transactional
    public OrderDto create(@RequestBody Order order) {
        Order orderSave;
        try {
            orderSave = orderRepo.save(order);
            BigDecimal totalPrice = BigDecimal.valueOf(0);
            Set<OrderDetail> orderDetailHashSet = new HashSet<>();
            for (CartItem cartItem : CartController.cartHashMap.values()) {
                totalPrice = totalPrice.add(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())));
                OrderDetail orderDetail = new OrderDetail(cartItem);
                orderDetail.setOrderId(orderSave.getId());
                orderDetailHashSet.add(orderDetail);
            }

            if (totalPrice.compareTo(BigDecimal.valueOf(0)) <= 0) {
                throw new RuntimeException(translationService.translate(ORDER_NOT_PRODUCT));
            }
            order.setTotalPrice(totalPrice);
            order.setCreatedAt(LocalDate.now());
            order.setPaymentStatus(Status.Payment.UNPAID.name());
            order.setOrderStatus(Status.Order.PENDING.name());
            order.setInventoryStatus(InventoryStatus.PENDING.name());
            order.setOrderDetails(orderDetailHashSet);

            rabbitTemplate.convertAndSend(DIRECT_EXCHANGE, DIRECT_SHARE_ROUTING_KEY, new OrderEvent(orderSave));
            cartController.clear();
            return new OrderDto(orderSave);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public Page<Order> getAll(int page, int pageSize) {
        if (page <= 0) {
            page = 1;
        }
        if (pageSize < 0) {
            page = 6;
        }
        return orderRepo.findAll(PageRequest.of(page - 1, pageSize, Sort.Direction.DESC, "id"));
    }

    @Override
    public Order findById(Long orderId) {
        return orderRepo.findById(orderId).orElse(null);
    }

    @Override
    public Order save(Order orderExist) {
        return orderRepo.save(orderExist);
    }


}
