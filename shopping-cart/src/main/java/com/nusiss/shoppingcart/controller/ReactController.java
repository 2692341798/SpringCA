package com.nusiss.shoppingcart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * React组件集成控制器
 * 用于在Spring Boot应用中展示React组件
 * @author SpringCA Team
 */
@Controller
@RequestMapping("/react")
public class ReactController {
    
    /**
     * React商品列表页面
     */
    @GetMapping("/products")
    public String reactProducts(Model model) {
        model.addAttribute("pageTitle", "React商品列表");
        model.addAttribute("pageDescription", "使用React技术实现的现代化商品列表页面");
        return "react/products";
    }
    
    /**
     * React购物车页面
     */
    @GetMapping("/cart")
    public String reactCart(Model model) {
        model.addAttribute("pageTitle", "React购物车");
        model.addAttribute("pageDescription", "使用React技术实现的购物车管理页面");
        return "react/cart";
    }
    
    /**
     * React演示首页
     */
    @GetMapping
    public String reactIndex(Model model) {
        model.addAttribute("pageTitle", "React功能演示");
        model.addAttribute("pageDescription", "SpringCA购物车系统的React功能演示");
        return "react/index";
    }
}