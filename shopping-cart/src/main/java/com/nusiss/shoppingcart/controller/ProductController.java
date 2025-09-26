package com.nusiss.shoppingcart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Collections;

@Controller
public class ProductController {

    @GetMapping("/products")
    public String listProducts(Model model) {
        model.addAttribute("products", Collections.emptyList());
        model.addAttribute("q", "");
        return "products";
    }
}