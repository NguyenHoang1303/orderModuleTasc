package com.example.ordermodule.controller;

import com.example.ordermodule.entity.CartItem;
import com.example.ordermodule.entity.Product;
import com.example.ordermodule.repo.ProductRepo;
import com.example.ordermodule.response.RESTResponse;
import com.example.ordermodule.service.CartServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;

@RestController
@RequestMapping("cart")
public class CartController {

    public static HashMap<Long, CartItem> cartHashMap = new HashMap<>();

    @Autowired
    ProductRepo productRepo;

    @Autowired
    CartServiceImpl cartService;

    @RequestMapping(method = RequestMethod.POST, path = "add")
    public ResponseEntity addToCart(@RequestParam(name = "id") int id) {
        CartItem cartItem = new CartItem();
        Product product = productRepo.findById((long) id).orElse(null);
        if (product == null) {
            return new ResponseEntity<>(new RESTResponse.SimpleError()
                    .build(), HttpStatus.OK);
        }
        cartItem.setQuantity(1);
        cartItem.setThumbnail(product.getThumbnail());
        cartItem.setProductId(product.getId());
        cartItem.setName(product.getName());
        cartItem.setUnitPrice(product.getPrice());

        CartItem cart = cartHashMap.putIfAbsent((long) id, cartItem);
        if (cart != null) {
            cart.setQuantity(cart.getQuantity() + 1);
        }
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(cartHashMap)
                .build(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, path = "test/addCart")
    public ResponseEntity test(@RequestBody CartItem cartItem) {
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(cartService.addToCart(cartItem))
                .build(), HttpStatus.OK);
    }


    @RequestMapping(method = RequestMethod.DELETE, path = "clear")
    public ResponseEntity clear() {
        cartService.clear();
        return new ResponseEntity<>(new RESTResponse.Success()
                .build(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "detail")
    public ResponseEntity getDetail() {
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(cartService.getDetail())
                .build(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "update")
    public ResponseEntity update(@RequestParam(name = "productId") int productId,
                                 @RequestParam(name = "quantity") int quantity
    ) {
        return new ResponseEntity<>(new RESTResponse.Success()
                .addData(cartService.updateCart(productId, quantity))
                .build(), HttpStatus.OK);
    }


}
