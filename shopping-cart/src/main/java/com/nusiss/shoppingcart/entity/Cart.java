package com.nusiss.shoppingcart.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 购物车实体类
 * @author SpringCA Team
 */
@Entity
@Table(name = "carts")
public class Cart {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<CartItem> cartItems = new ArrayList<>();
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 构造函数
    public Cart() {
    }
    
    public Cart(User user) {
        this.user = user;
        this.active = true;
    }
    
    // 业务方法
    /**
     * 计算购物车总金额
     */
    public BigDecimal getTotalAmount() {
        return cartItems.stream()
                .filter(item -> item.getProduct().getActive())
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    /**
     * 获取购物车商品总数量
     */
    public Integer getTotalQuantity() {
        return cartItems.stream()
                .filter(item -> item.getProduct().getActive())
                .mapToInt(CartItem::getQuantity)
                .sum();
    }
    
    /**
     * 添加商品到购物车
     */
    public void addItem(CartItem item) {
        item.setCart(this);
        this.cartItems.add(item);
    }
    
    /**
     * 从购物车移除商品
     */
    public void removeItem(CartItem item) {
        this.cartItems.remove(item);
        item.setCart(null);
    }
    
    /**
     * 清空购物车
     */
    public void clearItems() {
        this.cartItems.clear();
    }
    
    /**
     * 检查购物车是否为空
     */
    public boolean isEmpty() {
        return cartItems.isEmpty() || 
               cartItems.stream().noneMatch(item -> item.getProduct().getActive());
    }
    
    // Getter和Setter方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public User getUser() {
        return user;
    }
    
    public void setUser(User user) {
        this.user = user;
    }
    
    public List<CartItem> getCartItems() {
        return cartItems;
    }
    
    public void setCartItems(List<CartItem> cartItems) {
        this.cartItems = cartItems;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public void setActive(Boolean active) {
        this.active = active;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}