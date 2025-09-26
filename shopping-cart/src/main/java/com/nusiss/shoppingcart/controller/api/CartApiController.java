package com.nusiss.shoppingcart.controller.api;

import com.nusiss.shoppingcart.entity.CartItem;
import com.nusiss.shoppingcart.entity.User;
import com.nusiss.shoppingcart.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 购物车REST API控制器
 * 为React前端提供购物车相关的API接口
 * @author SpringCA Team
 */
@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class CartApiController {
    
    private static final Logger log = LoggerFactory.getLogger(CartApiController.class);
    
    private final CartService cartService;
    
    public CartApiController(CartService cartService) {
        this.cartService = cartService;
    }
    
    /**
     * 获取购物车信息
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getCart(HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户未登录");
                errorResponse.put("code", "UNAUTHORIZED");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            List<CartItem> cartItems = cartService.getCartItems(currentUser);
            BigDecimal totalAmount = cartService.getCartTotalAmount(currentUser);
            int totalQuantity = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of(
                "items", cartItems,
                "totalAmount", totalAmount,
                "totalQuantity", totalQuantity,
                "isEmpty", cartItems.isEmpty()
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取购物车信息失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取购物车信息失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 添加商品到购物车
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addToCart(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpSession session) {
        
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户未登录");
                errorResponse.put("code", "UNAUTHORIZED");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            if (quantity <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "商品数量必须大于0");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            boolean success = cartService.addToCart(currentUser, productId, quantity);
            
            if (success) {
                // 返回更新后的购物车信息
                List<CartItem> cartItems = cartService.getCartItems(currentUser);
                BigDecimal totalAmount = cartService.getCartTotalAmount(currentUser);
                int totalQuantity = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "商品已添加到购物车");
                response.put("data", Map.of(
                    "totalQuantity", totalQuantity,
                    "totalAmount", totalAmount
                ));
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "添加商品到购物车失败，可能是库存不足");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error("添加商品到购物车失败，商品ID: {}, 数量: {}", productId, quantity, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "添加商品到购物车失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 更新购物车商品数量
     */
    @PutMapping("/update")
    public ResponseEntity<Map<String, Object>> updateCartItem(
            @RequestParam Long cartItemId,
            @RequestParam Integer quantity,
            HttpSession session) {
        
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户未登录");
                errorResponse.put("code", "UNAUTHORIZED");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            if (quantity <= 0) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "商品数量必须大于0");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            boolean success = cartService.updateCartItemQuantity(currentUser, cartItemId, quantity);
            
            if (success) {
                // 返回更新后的购物车信息
                List<CartItem> cartItems = cartService.getCartItems(currentUser);
                BigDecimal totalAmount = cartService.getCartTotalAmount(currentUser);
                int totalQuantity = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "购物车已更新");
                response.put("data", Map.of(
                    "totalQuantity", totalQuantity,
                    "totalAmount", totalAmount
                ));
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "更新购物车失败，可能是库存不足或商品不存在");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error("更新购物车商品失败，商品项ID: {}, 数量: {}", cartItemId, quantity, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "更新购物车失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 删除购物车商品
     */
    @DeleteMapping("/remove/{cartItemId}")
    public ResponseEntity<Map<String, Object>> removeFromCart(
            @PathVariable Long cartItemId,
            HttpSession session) {
        
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户未登录");
                errorResponse.put("code", "UNAUTHORIZED");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            boolean success = cartService.removeFromCart(currentUser, cartItemId);
            
            if (success) {
                // 返回更新后的购物车信息
                List<CartItem> cartItems = cartService.getCartItems(currentUser);
                BigDecimal totalAmount = cartService.getCartTotalAmount(currentUser);
                int totalQuantity = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("message", "商品已从购物车中删除");
                response.put("data", Map.of(
                    "totalQuantity", totalQuantity,
                    "totalAmount", totalAmount,
                    "isEmpty", cartItems.isEmpty()
                ));
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "删除商品失败，商品可能不存在");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error("删除购物车商品失败，商品项ID: {}", cartItemId, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "删除商品失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 清空购物车
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearCart(HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "用户未登录");
                errorResponse.put("code", "UNAUTHORIZED");
                return ResponseEntity.status(401).body(errorResponse);
            }
            
            cartService.clearCart(currentUser);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "购物车已清空");
            response.put("data", Map.of(
                "totalQuantity", 0,
                "totalAmount", BigDecimal.ZERO,
                "isEmpty", true
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("清空购物车失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "清空购物车失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 获取购物车数量
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getCartCount(HttpSession session) {
        try {
            User currentUser = (User) session.getAttribute("currentUser");
            if (currentUser == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", Map.of("count", 0));
                return ResponseEntity.ok(response);
            }
            
            List<CartItem> cartItems = cartService.getCartItems(currentUser);
            int totalQuantity = cartItems.stream().mapToInt(CartItem::getQuantity).sum();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", Map.of("count", totalQuantity));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取购物车数量失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取购物车数量失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}