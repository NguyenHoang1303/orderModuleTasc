package com.example.ordermodule.service;

import com.example.ordermodule.entity.CartItem;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CartServiceImpl implements CartService {

    public static HashMap<Long, CartItem> cartHashMap = new HashMap<>();


    @Override
    public HashMap<Long, CartItem> addToCart(CartItem cartItem1) {
        CartItem item = cartHashMap.putIfAbsent(cartItem1.getProductId(), cartItem1);
        if (item != null) {
            item.setQuantity(item.getQuantity() + 1);
        }
        return cartHashMap;
    }

    @Override
    public void clear() {
        cartHashMap.clear();
    }

    @Override
    public HashMap<Long, CartItem> getDetail() {
        return cartHashMap;
    }

    @Override
    public HashMap<Long, CartItem> updateCart(int productId, int quantity) {
        CartItem cartItem = cartHashMap.get((long) productId);
        if (quantity <= 0 || cartItem == null) {
            throw new RuntimeException("không tìm thấy sản phẩm trong giỏ hang");
        }
        cartItem.setQuantity(quantity);
        return cartHashMap;
    }
}
