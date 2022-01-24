package com.example.ordermodule.controller;


import com.example.ordermodule.translate.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("trans")
@CrossOrigin("*")
public class Trans {

    @Autowired
    TranslationService translationService;

    @RequestMapping(method = RequestMethod.GET)
    public String getMessage(@RequestParam(name = "key") String key) {
        return translationService.translate(key);
    }
}
