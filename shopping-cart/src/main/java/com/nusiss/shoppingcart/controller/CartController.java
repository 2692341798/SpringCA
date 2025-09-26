package com.nusiss.shoppingcart.controller;

import com.nusiss.shoppingcart.entity.CartItem;
import com.nusiss.shoppingcart.entity.User;
import com.nusiss.shoppingcart.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车控制器
 * @author SpringCA Team
 */
@Controller
@RequestMapping("/cart")
public class CartController {
    
    private static final Logger log = LoggerFactory.getLogger(CartController.class);
    
    private final CartService cartService;
    
    public CartController(CartService cartService) {
        this.cartService = cartService;
    }
    
    /**
     * 显示购物车页面
     */
    @GetMapping
    public String showCart(HttpSession session, Model model) {
        long startTime = System.currentTimeMillis();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        try {
            // 并行获取购物车数据以提高性能
            List<CartItem> cartItems = cartService.getCartItems(currentUser);
            
            // 如果购物车为空，直接返回
            if (cartItems.isEmpty()) {
                model.addAttribute("cartItems", cartItems);
                model.addAttribute("totalAmount", BigDecimal.ZERO);
                model.addAttribute("totalQuantity", 0);
                model.addAttribute("isEmpty", true);
                
                long endTime = System.currentTimeMillis();
                log.debug("购物车页面加载时间: {}ms (空购物车)", endTime - startTime);
                return "cart/cart";
            }
            
            // 计算总金额和数量
            BigDecimal totalAmount = cartService.getCartTotalAmount(currentUser);
            Integer totalQuantity = cartService.getCartTotalQuantity(currentUser);
            
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("totalQuantity", totalQuantity);
            model.addAttribute("isEmpty", false);
            
            long endTime = System.currentTimeMillis();
            log.debug("购物车页面加载时间: {}ms", endTime - startTime);
            
            return "cart/cart";
        } catch (Exception e) {
            log.error("显示购物车页面失败：{}", e.getMessage());
            model.addAttribute("error", "加载购物车失败，请稍后重试");
            model.addAttribute("isEmpty", true);
            return "cart/cart";
        }
    }
    
    /**
     * 添加商品到购物车（AJAX）
     */
    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addToCart(@RequestParam Long productId,
                                                        @RequestParam(defaultValue = "1") Integer quantity,
                                                        HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            response.put("redirect", "/auth/login");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean success = cartService.addToCart(currentUser, productId, quantity);
            if (success) {
                Integer totalQuantity = cartService.getCartTotalQuantity(currentUser);
                response.put("success", true);
                response.put("message", "商品已添加到购物车");
                response.put("cartQuantity", totalQuantity);
            } else {
                response.put("success", false);
                response.put("message", "添加失败，可能是库存不足或商品不存在");
            }
        } catch (Exception e) {
            log.error("添加商品到购物车失败：{}", e.getMessage());
            response.put("success", false);
            response.put("message", "添加失败，请稍后重试");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新购物车商品数量（AJAX）
     */
    @PostMapping("/update")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> updateCartItem(@RequestParam Long cartItemId,
                                                             @RequestParam Integer quantity,
                                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean success = cartService.updateCartItemQuantity(currentUser, cartItemId, quantity);
            if (success) {
                BigDecimal totalAmount = cartService.getCartTotalAmount(currentUser);
                Integer totalQuantity = cartService.getCartTotalQuantity(currentUser);
                
                response.put("success", true);
                response.put("message", "数量已更新");
                response.put("totalAmount", totalAmount);
                response.put("cartQuantity", totalQuantity);
            } else {
                response.put("success", false);
                response.put("message", "更新失败，可能是库存不足");
            }
        } catch (Exception e) {
            log.error("更新购物车商品数量失败：{}", e.getMessage());
            response.put("success", false);
            response.put("message", "更新失败，请稍后重试");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 从购物车移除商品（AJAX）
     */
    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> removeFromCart(@RequestParam Long cartItemId,
                                                             HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean success = cartService.removeFromCart(currentUser, cartItemId);
            if (success) {
                BigDecimal totalAmount = cartService.getCartTotalAmount(currentUser);
                Integer totalQuantity = cartService.getCartTotalQuantity(currentUser);
                boolean isEmpty = cartService.isCartEmpty(currentUser);
                
                response.put("success", true);
                response.put("message", "商品已移除");
                response.put("totalAmount", totalAmount);
                response.put("cartQuantity", totalQuantity);
                response.put("isEmpty", isEmpty);
            } else {
                response.put("success", false);
                response.put("message", "移除失败");
            }
        } catch (Exception e) {
            log.error("从购物车移除商品失败：{}", e.getMessage());
            response.put("success", false);
            response.put("message", "移除失败，请稍后重试");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 清空购物车（AJAX）
     */
    @PostMapping("/clear")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> clearCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean success = cartService.clearCart(currentUser);
            if (success) {
                response.put("success", true);
                response.put("message", "购物车已清空");
                response.put("cartQuantity", 0);
            } else {
                response.put("success", false);
                response.put("message", "清空失败");
            }
        } catch (Exception e) {
            log.error("清空购物车失败：{}", e.getMessage());
            response.put("success", false);
            response.put("message", "清空失败，请稍后重试");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取购物车数量（AJAX）
     */
    @GetMapping("/count")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getCartCount(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("count", 0);
            return ResponseEntity.ok(response);
        }
        
        try {
            Integer totalQuantity = cartService.getCartTotalQuantity(currentUser);
            response.put("count", totalQuantity);
        } catch (Exception e) {
            log.error("获取购物车数量失败：{}", e.getMessage());
            response.put("count", 0);
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 验证购物车库存（结账前调用）
     */
    @GetMapping("/validate")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> validateCart(HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("valid", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean valid = cartService.validateCartStock(currentUser);
            response.put("valid", valid);
            if (!valid) {
                response.put("message", "购物车中有商品库存不足，请调整数量");
            }
        } catch (Exception e) {
            log.error("验证购物车库存失败：{}", e.getMessage());
            response.put("valid", false);
            response.put("message", "验证失败，请稍后重试");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 快速添加商品到购物车（从商品详情页）
     */
    @PostMapping("/quick-add")
    public String quickAddToCart(@RequestParam Long productId,
                                @RequestParam(defaultValue = "1") Integer quantity,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        try {
            boolean success = cartService.addToCart(currentUser, productId, quantity);
            if (success) {
                redirectAttributes.addFlashAttribute("success", "商品已添加到购物车");
            } else {
                redirectAttributes.addFlashAttribute("error", "添加失败，可能是库存不足或商品不存在");
            }
        } catch (Exception e) {
            log.error("快速添加商品到购物车失败：{}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "添加失败，请稍后重试");
        }
        
        return "redirect:/products/" + productId;
    }
}