package com.example.ordermodule.service;

import com.example.ordermodule.entity.CartItem;

import java.util.HashMap;

public interface CartService {
    HashMap<Long, CartItem> addToCart(CartItem cartItem1);

    void clear();

    HashMap<Long, CartItem> getDetail();

    HashMap<Long, CartItem> updateCart(int productId, int quantity);
}
