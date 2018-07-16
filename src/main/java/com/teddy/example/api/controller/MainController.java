package com.teddy.example.api.controller;

import com.teddy.example.api.service.MainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class MainController {

    @Autowired
    private MainService mainService;

    @PostMapping("/pub//{key}")
    public String publish(@PathVariable(value = "key") String key,
                          @RequestParam(value = "value") String value) {
        Long publish = mainService.publish(key, value);
        return "publish success : " + publish;
    }

    @GetMapping("/string/{key}")
    public String getByString(@PathVariable(value = "key") String key) {
        String value = mainService.get(key, String.class);

        return "string value : " + value;
    }

    @GetMapping("/boolean/{key}")
    public String getByBoolean(@PathVariable(value = "key") String key) {
        Boolean value = mainService.get(key, Boolean.class);

        return "string value : " + value;
    }
}
