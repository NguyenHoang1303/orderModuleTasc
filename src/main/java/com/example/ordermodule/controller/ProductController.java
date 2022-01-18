package com.example.ordermodule.controller;


import com.example.ordermodule.entity.Product;
import com.example.ordermodule.repo.ProductRepo;
import com.example.ordermodule.response.RESTPagination;
import com.example.ordermodule.response.RESTResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    ProductRepo productRepo;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getAll(@RequestParam(name = "page", defaultValue = "1") int page,
                                @RequestParam(name = "pageSize", defaultValue = "9") int pageSize
    ){
        if (page <= 0 ){
            page = 1;
        }
        if (pageSize < 0){
            page = 9;
        }

        Page paging = productRepo.findAll(PageRequest.of(page - 1, pageSize));
        return new ResponseEntity<>(new RESTResponse.Success()
                .setPagination(new RESTPagination(paging.getNumber() + 1, paging.getSize(), paging.getTotalElements()))
                .addData(paging.getContent())
                .buildData(), HttpStatus.OK);
    }
}
