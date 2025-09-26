package com.nusiss.shoppingcart.service;

import com.nusiss.shoppingcart.entity.Cart;
import com.nusiss.shoppingcart.entity.CartItem;
import com.nusiss.shoppingcart.entity.Product;
import com.nusiss.shoppingcart.entity.User;
import com.nusiss.shoppingcart.repository.CartItemRepository;
import com.nusiss.shoppingcart.repository.CartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 购物车业务逻辑层
 * @author SpringCA Team
 */
@Service
@Transactional(readOnly = true)
public class CartService {
    
    private static final Logger log = LoggerFactory.getLogger(CartService.class);
    
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductService productService;
    
    public CartService(CartRepository cartRepository, 
                      CartItemRepository cartItemRepository,
                      ProductService productService) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productService = productService;
    }
    
    /**
     * 获取用户的购物车
     * @param user 用户对象
     * @return 购物车对象
     */
    public Cart getOrCreateCart(User user) {
        Optional<Cart> cartOpt = cartRepository.findByUserAndActiveTrue(user);
        if (cartOpt.isPresent()) {
            return cartOpt.get();
        } else {
            // 创建新的购物车
            Cart cart = new Cart(user);
            return cartRepository.save(cart);
        }
    }
    
    /**
     * 根据用户ID获取购物车
     * @param userId 用户ID
     * @return 购物车对象
     */
    public Optional<Cart> getCartByUserId(Long userId) {
        return cartRepository.findByUserIdAndActiveTrue(userId);
    }
    
    /**
     * 添加商品到购物车
     * @param user 用户对象
     * @param productId 商品ID
     * @param quantity 数量
     * @return 是否添加成功
     */
    @Transactional
    public boolean addToCart(User user, Long productId, Integer quantity) {
        try {
            // 验证商品是否存在且有效
            Optional<Product> productOpt = productService.getProductById(productId);
            if (productOpt.isEmpty() || !productOpt.get().getActive()) {
                log.warn("商品不存在或已下架：{}", productId);
                return false;
            }
            
            Product product = productOpt.get();
            
            // 检查库存
            if (!product.hasStock(quantity)) {
                log.warn("商品库存不足：{}, 请求数量：{}, 可用库存：{}", 
                        productId, quantity, product.getStock());
                return false;
            }
            
            // 获取或创建购物车
            Cart cart = getOrCreateCart(user);
            
            // 检查购物车中是否已有该商品
            Optional<CartItem> existingItemOpt = cartItemRepository.findByCartAndProduct(cart, product);
            
            if (existingItemOpt.isPresent()) {
                // 更新现有商品数量
                CartItem existingItem = existingItemOpt.get();
                int newQuantity = existingItem.getQuantity() + quantity;
                
                // 再次检查库存
                if (!product.hasStock(newQuantity)) {
                    log.warn("商品库存不足：{}, 购物车现有：{}, 新增：{}, 可用库存：{}", 
                            productId, existingItem.getQuantity(), quantity, product.getStock());
                    return false;
                }
                
                existingItem.setQuantity(newQuantity);
                cartItemRepository.save(existingItem);
                log.info("更新购物车商品数量：用户 {}, 商品 {}, 新数量 {}", 
                        user.getId(), productId, newQuantity);
            } else {
                // 添加新商品到购物车
                CartItem cartItem = new CartItem(cart, product, quantity);
                cartItemRepository.save(cartItem);
                log.info("添加商品到购物车：用户 {}, 商品 {}, 数量 {}", 
                        user.getId(), productId, quantity);
            }
            
            return true;
        } catch (Exception e) {
            log.error("添加商品到购物车失败：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 更新购物车商品数量
     * @param user 用户对象
     * @param cartItemId 购物车项ID
     * @param quantity 新数量
     * @return 是否更新成功
     */
    @Transactional
    public boolean updateCartItemQuantity(User user, Long cartItemId, Integer quantity) {
        try {
            Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
            if (cartItemOpt.isEmpty()) {
                log.warn("购物车项不存在：{}", cartItemId);
                return false;
            }
            
            CartItem cartItem = cartItemOpt.get();
            
            // 验证购物车项属于当前用户
            if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
                log.warn("购物车项不属于当前用户：用户 {}, 购物车项 {}", user.getId(), cartItemId);
                return false;
            }
            
            // 如果数量为0，删除该项
            if (quantity <= 0) {
                return removeFromCart(user, cartItemId);
            }
            
            // 检查库存
            if (!cartItem.getProduct().hasStock(quantity)) {
                log.warn("商品库存不足：{}, 请求数量：{}, 可用库存：{}", 
                        cartItem.getProduct().getId(), quantity, cartItem.getProduct().getStock());
                return false;
            }
            
            cartItem.setQuantity(quantity);
            cartItemRepository.save(cartItem);
            log.info("更新购物车商品数量：用户 {}, 购物车项 {}, 新数量 {}", 
                    user.getId(), cartItemId, quantity);
            
            return true;
        } catch (Exception e) {
            log.error("更新购物车商品数量失败：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从购物车移除商品
     * @param user 用户对象
     * @param cartItemId 购物车项ID
     * @return 是否移除成功
     */
    @Transactional
    public boolean removeFromCart(User user, Long cartItemId) {
        try {
            Optional<CartItem> cartItemOpt = cartItemRepository.findById(cartItemId);
            if (cartItemOpt.isEmpty()) {
                log.warn("购物车项不存在：{}", cartItemId);
                return false;
            }
            
            CartItem cartItem = cartItemOpt.get();
            
            // 验证购物车项属于当前用户
            if (!cartItem.getCart().getUser().getId().equals(user.getId())) {
                log.warn("购物车项不属于当前用户：用户 {}, 购物车项 {}", user.getId(), cartItemId);
                return false;
            }
            
            cartItemRepository.delete(cartItem);
            log.info("从购物车移除商品：用户 {}, 购物车项 {}", user.getId(), cartItemId);
            
            return true;
        } catch (Exception e) {
            log.error("从购物车移除商品失败：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 清空购物车
     * @param user 用户对象
     * @return 是否清空成功
     */
    @Transactional
    public boolean clearCart(User user) {
        try {
            Optional<Cart> cartOpt = cartRepository.findByUserAndActiveTrue(user);
            if (cartOpt.isEmpty()) {
                log.info("用户购物车为空：{}", user.getId());
                return true;
            }
            
            Cart cart = cartOpt.get();
            cartItemRepository.deleteByCart(cart);
            log.info("清空购物车：用户 {}", user.getId());
            
            return true;
        } catch (Exception e) {
            log.error("清空购物车失败：{}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 获取购物车商品列表
     * @param user 用户对象
     * @return 购物车商品列表
     */
    public List<CartItem> getCartItems(User user) {
        Optional<Cart> cartOpt = cartRepository.findByUserAndActiveTrue(user);
        if (cartOpt.isPresent()) {
            return cartItemRepository.findByCart(cartOpt.get());
        }
        return List.of();
    }
    
    /**
     * 获取购物车总金额
     * @param user 用户对象
     * @return 总金额
     */
    public BigDecimal getCartTotalAmount(User user) {
        Optional<Cart> cartOpt = cartRepository.findByUserAndActiveTrue(user);
        if (cartOpt.isPresent()) {
            return cartOpt.get().getTotalAmount();
        }
        return BigDecimal.ZERO;
    }
    
    /**
     * 获取购物车商品总数量
     * @param user 用户对象
     * @return 总数量
     */
    public Integer getCartTotalQuantity(User user) {
        Optional<Cart> cartOpt = cartRepository.findByUserAndActiveTrue(user);
        if (cartOpt.isPresent()) {
            return cartOpt.get().getTotalQuantity();
        }
        return 0;
    }
    
    /**
     * 检查购物车是否为空
     * @param user 用户对象
     * @return 是否为空
     */
    public boolean isCartEmpty(User user) {
        Optional<Cart> cartOpt = cartRepository.findByUserAndActiveTrue(user);
        return cartOpt.isEmpty() || cartOpt.get().isEmpty();
    }
    
    /**
     * 验证购物车商品库存
     * @param user 用户对象
     * @return 是否所有商品库存充足
     */
    public boolean validateCartStock(User user) {
        List<CartItem> cartItems = getCartItems(user);
        for (CartItem item : cartItems) {
            if (!item.hasEnoughStock()) {
                log.warn("购物车商品库存不足：商品 {}, 需要 {}, 可用 {}", 
                        item.getProduct().getId(), item.getQuantity(), item.getProduct().getStock());
                return false;
            }
        }
        return true;
    }
}