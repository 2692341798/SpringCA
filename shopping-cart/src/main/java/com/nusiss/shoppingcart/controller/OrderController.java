package com.nusiss.shoppingcart.controller;

import com.nusiss.shoppingcart.entity.CartItem;
import com.nusiss.shoppingcart.entity.Order;
import com.nusiss.shoppingcart.entity.OrderItem;
import com.nusiss.shoppingcart.entity.User;
import com.nusiss.shoppingcart.service.CartService;
import com.nusiss.shoppingcart.service.OrderService;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 订单控制器
 * @author SpringCA Team
 */
@Controller
@RequestMapping("/orders")
public class OrderController {
    
    private static final Logger log = LoggerFactory.getLogger(OrderController.class);
    
    private final OrderService orderService;
    private final CartService cartService;
    
    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }
    
    /**
     * 显示结账页面
     */
    @GetMapping("/checkout")
    public String showCheckout(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        try {
            // 检查购物车是否为空
            if (cartService.isCartEmpty(currentUser)) {
                return "redirect:/cart";
            }
            
            // 验证购物车库存
            if (!cartService.validateCartStock(currentUser)) {
                model.addAttribute("error", "购物车中有商品库存不足，请返回购物车调整");
                return "redirect:/cart";
            }
            
            List<CartItem> cartItems = cartService.getCartItems(currentUser);
            BigDecimal totalAmount = cartService.getCartTotalAmount(currentUser);
            
            model.addAttribute("cartItems", cartItems);
            model.addAttribute("totalAmount", totalAmount);
            model.addAttribute("user", currentUser);
            
            return "orders/checkout";
        } catch (Exception e) {
            log.error("显示结账页面失败：{}", e.getMessage());
            model.addAttribute("error", "加载结账页面失败，请稍后重试");
            return "redirect:/cart";
        }
    }
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public String createOrder(@RequestParam String shippingAddress,
                             @RequestParam String recipientName,
                             @RequestParam String recipientPhone,
                             @RequestParam(defaultValue = "在线支付") String paymentMethod,
                             @RequestParam(defaultValue = "") String notes,
                             HttpSession session,
                             RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        try {
            // 验证必填字段
            if (shippingAddress == null || shippingAddress.trim().isEmpty() ||
                recipientName == null || recipientName.trim().isEmpty() ||
                recipientPhone == null || recipientPhone.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "请填写完整的收货信息");
                return "redirect:/orders/checkout";
            }
            
            Optional<Order> orderOpt = orderService.createOrder(
                currentUser, 
                shippingAddress.trim(), 
                recipientName.trim(), 
                recipientPhone.trim(), 
                paymentMethod, 
                notes.trim()
            );
            
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                redirectAttributes.addFlashAttribute("success", "订单创建成功！订单号：" + order.getOrderNumber());
                return "redirect:/orders/" + order.getOrderNumber();
            } else {
                redirectAttributes.addFlashAttribute("error", "订单创建失败，请检查商品库存");
                return "redirect:/orders/checkout";
            }
        } catch (Exception e) {
            log.error("创建订单失败：{}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "订单创建失败，请稍后重试");
            return "redirect:/orders/checkout";
        }
    }
    
    /**
     * 显示订单详情页面
     */
    @GetMapping("/{orderNumber}")
    public String showOrderDetail(@PathVariable String orderNumber,
                                 HttpSession session,
                                 Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Optional<Order> orderOpt = orderService.getOrderByNumber(orderNumber);
            if (orderOpt.isEmpty()) {
                model.addAttribute("error", "订单不存在");
                return "orders/order-not-found";
            }
            
            Order order = orderOpt.get();
            
            // 验证订单属于当前用户
            if (!order.getUser().getId().equals(currentUser.getId())) {
                model.addAttribute("error", "无权访问此订单");
                return "orders/order-not-found";
            }
            
            List<OrderItem> orderItems = orderService.getOrderItems(order);
            
            model.addAttribute("order", order);
            model.addAttribute("orderItems", orderItems);
            
            return "orders/order-detail";
        } catch (Exception e) {
            log.error("显示订单详情失败：{}", e.getMessage());
            model.addAttribute("error", "加载订单详情失败，请稍后重试");
            return "orders/order-not-found";
        }
    }
    
    /**
     * 显示订单历史页面
     */
    @GetMapping
    public String showOrderHistory(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "10") int size,
                                  @RequestParam(defaultValue = "") String status,
                                  HttpSession session,
                                  Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        try {
            Page<Order> orderPage;
            
            if (status.isEmpty()) {
                orderPage = orderService.getUserOrders(currentUser, page, size);
            } else {
                try {
                    Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
                    orderPage = orderService.getUserOrdersByStatus(currentUser, orderStatus, page, size);
                } catch (IllegalArgumentException e) {
                    orderPage = orderService.getUserOrders(currentUser, page, size);
                }
            }
            
            model.addAttribute("orderPage", orderPage);
            model.addAttribute("currentStatus", status);
            model.addAttribute("orderStatuses", Order.OrderStatus.values());
            
            return "orders/order-history";
        } catch (Exception e) {
            log.error("显示订单历史失败：{}", e.getMessage());
            model.addAttribute("error", "加载订单历史失败，请稍后重试");
            return "orders/order-history";
        }
    }
    
    /**
     * 支付订单（AJAX）
     */
    @PostMapping("/{orderNumber}/pay")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> payOrder(@PathVariable String orderNumber,
                                                       HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean success = orderService.payOrder(orderNumber, currentUser);
            if (success) {
                response.put("success", true);
                response.put("message", "支付成功");
            } else {
                response.put("success", false);
                response.put("message", "支付失败，请检查订单状态");
            }
        } catch (Exception e) {
            log.error("支付订单失败：{}", e.getMessage());
            response.put("success", false);
            response.put("message", "支付失败，请稍后重试");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 取消订单（AJAX）
     */
    @PostMapping("/{orderNumber}/cancel")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> cancelOrder(@PathVariable String orderNumber,
                                                          HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean success = orderService.cancelOrder(orderNumber, currentUser);
            if (success) {
                response.put("success", true);
                response.put("message", "订单已取消");
            } else {
                response.put("success", false);
                response.put("message", "取消失败，请检查订单状态");
            }
        } catch (Exception e) {
            log.error("取消订单失败：{}", e.getMessage());
            response.put("success", false);
            response.put("message", "取消失败，请稍后重试");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 确认收货（AJAX）
     */
    @PostMapping("/{orderNumber}/confirm")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> confirmDelivery(@PathVariable String orderNumber,
                                                              HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            response.put("success", false);
            response.put("message", "请先登录");
            return ResponseEntity.ok(response);
        }
        
        try {
            boolean success = orderService.confirmDelivery(orderNumber, currentUser);
            if (success) {
                response.put("success", true);
                response.put("message", "确认收货成功");
            } else {
                response.put("success", false);
                response.put("message", "确认失败，请检查订单状态");
            }
        } catch (Exception e) {
            log.error("确认收货失败：{}", e.getMessage());
            response.put("success", false);
            response.put("message", "确认失败，请稍后重试");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 快速下单（从商品详情页直接购买）
     */
    @PostMapping("/quick-order")
    public String quickOrder(@RequestParam Long productId,
                            @RequestParam(defaultValue = "1") Integer quantity,
                            @RequestParam String shippingAddress,
                            @RequestParam String recipientName,
                            @RequestParam String recipientPhone,
                            @RequestParam(defaultValue = "在线支付") String paymentMethod,
                            @RequestParam(defaultValue = "") String notes,
                            HttpSession session,
                            RedirectAttributes redirectAttributes) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/auth/login";
        }
        
        try {
            // 先添加到购物车
            boolean addSuccess = cartService.addToCart(currentUser, productId, quantity);
            if (!addSuccess) {
                redirectAttributes.addFlashAttribute("error", "商品库存不足或不存在");
                return "redirect:/products/" + productId;
            }
            
            // 创建订单
            Optional<Order> orderOpt = orderService.createOrder(
                currentUser, 
                shippingAddress.trim(), 
                recipientName.trim(), 
                recipientPhone.trim(), 
                paymentMethod, 
                notes.trim()
            );
            
            if (orderOpt.isPresent()) {
                Order order = orderOpt.get();
                redirectAttributes.addFlashAttribute("success", "订单创建成功！订单号：" + order.getOrderNumber());
                return "redirect:/orders/" + order.getOrderNumber();
            } else {
                redirectAttributes.addFlashAttribute("error", "订单创建失败");
                return "redirect:/products/" + productId;
            }
        } catch (Exception e) {
            log.error("快速下单失败：{}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "下单失败，请稍后重试");
            return "redirect:/products/" + productId;
        }
    }
}