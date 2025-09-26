package com.nusiss.shoppingcart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController {

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("appName", "Spring Shopping Cart");
        return "index";
    }

    @GetMapping("/health")
    @ResponseBody
    public String health() {
        return "OK";
    }
}