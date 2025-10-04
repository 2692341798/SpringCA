package com.nusiss.shoppingcart.service;

import com.nusiss.shoppingcart.entity.*;
import com.nusiss.shoppingcart.repository.OrderItemRepository;
import com.nusiss.shoppingcart.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 订单业务逻辑层
 * @author SpringCA Team
 */
@Service
@Transactional(readOnly = true)
public class OrderService {
    
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);
    
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;
    private final ProductService productService;
    
    public OrderService(OrderRepository orderRepository,
                       OrderItemRepository orderItemRepository,
                       CartService cartService,
                       ProductService productService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartService = cartService;
        this.productService = productService;
    }
    
    /**
     * 创建订单
     * @param user 用户对象
     * @param shippingAddress 收货地址
     * @param recipientName 收货人姓名
     * @param recipientPhone 收货人电话
     * @param paymentMethod 支付方式
     * @param notes 备注
     * @return 订单对象
     */
    @Transactional
    public Optional<Order> createOrder(User user, String shippingAddress, String recipientName, 
                                     String recipientPhone, String paymentMethod, String notes) {
        try {
            // 获取购物车商品
            List<CartItem> cartItems = cartService.getCartItems(user);
            if (cartItems.isEmpty()) {
                log.warn("购物车为空，无法创建订单：用户 {}", user.getId());
                return Optional.empty();
            }
            
            // 验证库存
            if (!cartService.validateCartStock(user)) {
                log.warn("购物车商品库存不足，无法创建订单：用户 {}", user.getId());
                return Optional.empty();
            }
            
            // 生成订单号
            String orderNumber = generateOrderNumber();
            
            // 创建订单
            Order order = new Order(user, orderNumber);
            order.setShippingAddress(shippingAddress);
            order.setRecipientName(recipientName);
            order.setRecipientPhone(recipientPhone);
            order.setPaymentMethod(paymentMethod);
            order.setNotes(notes);
            
            // 保存订单
            order = orderRepository.save(order);
            
            // 创建订单项并扣减库存
            for (CartItem cartItem : cartItems) {
                // 再次检查库存（防止并发问题）
                if (!productService.checkStock(cartItem.getProduct().getId(), cartItem.getQuantity())) {
                    log.error("创建订单时库存不足：商品 {}, 需要 {}", 
                            cartItem.getProduct().getId(), cartItem.getQuantity());
                    throw new RuntimeException("库存不足：" + cartItem.getProduct().getName());
                }
                
                // 扣减库存
                if (!productService.reduceStock(cartItem.getProduct().getId(), cartItem.getQuantity())) {
                    log.error("扣减库存失败：商品 {}, 数量 {}", 
                            cartItem.getProduct().getId(), cartItem.getQuantity());
                    throw new RuntimeException("扣减库存失败：" + cartItem.getProduct().getName());
                }
                
                // 创建订单项
                OrderItem orderItem = OrderItem.fromCartItem(cartItem);
                orderItem.setOrder(order);
                order.addOrderItem(orderItem);  // 使用addOrderItem方法，会自动计算总金额
                orderItemRepository.save(orderItem);
            }
            
            // 保存订单（总金额已在addOrderItem中计算）
            order = orderRepository.save(order);
            
            // 清空购物车
            cartService.clearCart(user);
            
            log.info("订单创建成功：订单号 {}, 用户 {}, 金额 {}", 
                    orderNumber, user.getId(), order.getTotalAmount());
            
            return Optional.of(order);
            
        } catch (Exception e) {
            log.error("创建订单失败：{}", e.getMessage());
            return Optional.empty();
        }
    }
    
    /**
     * 根据订单号查找订单
     * @param orderNumber 订单号
     * @return 订单对象
     */
    public Optional<Order> getOrderByNumber(String orderNumber) {
        return orderRepository.findByOrderNumberWithOrderItems(orderNumber);
    }
    
    /**
     * 根据订单ID查找订单
     * @param orderId 订单ID
     * @return 订单对象
     */
    public Optional<Order> getOrderById(Long orderId) {
        return orderRepository.findById(orderId);
    }
    
    /**
     * 获取用户的订单列表（分页）
     * @param user 用户对象
     * @param page 页码
     * @param size 每页大小
     * @return 订单分页结果
     */
    public Page<Order> getUserOrders(User user, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByUserWithOrderItems(user, pageable);
    }
    
    /**
     * 根据状态获取用户的订单列表（分页）
     * @param user 用户对象
     * @param status 订单状态
     * @param page 页码
     * @param size 每页大小
     * @return 订单分页结果
     */
    public Page<Order> getUserOrdersByStatus(User user, Order.OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByUserAndStatusWithOrderItems(user, status, pageable);
    }
    
    /**
     * 获取订单的商品项列表
     * @param order 订单对象
     * @return 订单项列表
     */
    public List<OrderItem> getOrderItems(Order order) {
        return orderItemRepository.findByOrder(order);
    }
    
    /**
     * 支付订单
     * @param orderNumber 订单号
     * @param user 用户对象
     * @return 是否支付成功
     */
    @Transactional
    public boolean payOrder(String orderNumber, User user) {
        try {
            Optional<Order> orderOpt = orderRepository.findByOrderNumberWithOrderItems(orderNumber);
            if (orderOpt.isEmpty()) {
                log.warn("订单不存在：{}", orderNumber);
                return false;
            }
            
            Order order = orderOpt.get();
            
            // 验证订单属于当前用户
            if (!order.getUser().getId().equals(user.getId())) {
                log.warn("订单不属于当前用户：用户 {}, 订单 {}", user.getId(), orderNumber);
                return false;
            }
            
            // 检查订单状态
            if (order.getStatus() != Order.OrderStatus.PENDING) {
                log.warn("订单状态不允许支付：订单 {}, 状态 {}", orderNumber, order.getStatus());
                return false;
            }
            
            // 标记为已支付
            order.markAsPaid();
            orderRepository.save(order);
            
            log.info("订单支付成功：订单号 {}, 用户 {}", orderNumber, user.getId());
            return true;
            
        } catch (Exception e) {
            log.error("订单支付失败：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 取消订单
     * @param orderNumber 订单号
     * @param user 用户对象
     * @return 是否取消成功
     */
    @Transactional
    public boolean cancelOrder(String orderNumber, User user) {
        try {
            Optional<Order> orderOpt = orderRepository.findByOrderNumberWithOrderItems(orderNumber);
            if (orderOpt.isEmpty()) {
                log.warn("订单不存在：{}", orderNumber);
                return false;
            }
            
            Order order = orderOpt.get();
            
            // 验证订单属于当前用户
            if (!order.getUser().getId().equals(user.getId())) {
                log.warn("订单不属于当前用户：用户 {}, 订单 {}", user.getId(), orderNumber);
                return false;
            }
            
            // 检查订单是否可以取消
            if (!order.canBeCancelled()) {
                log.warn("订单状态不允许取消：订单 {}, 状态 {}", orderNumber, order.getStatus());
                return false;
            }
            
            // 恢复库存
            List<OrderItem> orderItems = orderItemRepository.findByOrder(order);
            for (OrderItem orderItem : orderItems) {
                productService.addStock(orderItem.getProduct().getId(), orderItem.getQuantity());
            }
            
            // 标记为已取消
            order.markAsCancelled();
            orderRepository.save(order);
            
            log.info("订单取消成功：订单号 {}, 用户 {}", orderNumber, user.getId());
            return true;
            
        } catch (Exception e) {
            log.error("订单取消失败：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 确认收货
     * @param orderNumber 订单号
     * @param user 用户对象
     * @return 是否确认成功
     */
    @Transactional
    public boolean confirmDelivery(String orderNumber, User user) {
        try {
            Optional<Order> orderOpt = orderRepository.findByOrderNumberWithOrderItems(orderNumber);
            if (orderOpt.isEmpty()) {
                log.warn("订单不存在：{}", orderNumber);
                return false;
            }
            
            Order order = orderOpt.get();
            
            // 验证订单属于当前用户
            if (!order.getUser().getId().equals(user.getId())) {
                log.warn("订单不属于当前用户：用户 {}, 订单 {}", user.getId(), orderNumber);
                return false;
            }
            
            // 检查订单状态
            if (order.getStatus() != Order.OrderStatus.SHIPPED) {
                log.warn("订单状态不允许确认收货：订单 {}, 状态 {}", orderNumber, order.getStatus());
                return false;
            }
            
            // 标记为已完成
            order.markAsDelivered();
            orderRepository.save(order);
            
            log.info("确认收货成功：订单号 {}, 用户 {}", orderNumber, user.getId());
            return true;
            
        } catch (Exception e) {
            log.error("确认收货失败：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 生成订单号
     * @return 订单号
     */
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        return "ORD" + timestamp + uuid;
    }
    
    /**
     * 统计用户订单数量
     * @param user 用户对象
     * @return 订单数量
     */
    public Long countUserOrders(User user) {
        return orderRepository.countByUser(user);
    }
    
    /**
     * 获取所有订单（管理员功能）
     * @param page 页码
     * @param size 每页大小
     * @return 订单分页结果
     */
    public Page<Order> getAllOrders(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findAll(pageable);
    }
    
    /**
     * 根据状态获取订单（管理员功能）
     * @param status 订单状态
     * @param page 页码
     * @param size 每页大小
     * @return 订单分页结果
     */
    public Page<Order> getOrdersByStatus(Order.OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return orderRepository.findByStatus(status, pageable);
    }
}