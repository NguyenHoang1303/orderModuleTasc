package com.example.ordermodule.controller;

import com.example.ordermodule.translate.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("translate")
public class TranslatorController {

    @Autowired
    TranslationService translationService;

    @RequestMapping(method = RequestMethod.GET)
    public String translate(@RequestParam String key){
        return translationService.translate(key);
    }
}
