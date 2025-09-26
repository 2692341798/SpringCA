package com.nusiss.shoppingcart.repository;

import com.nusiss.shoppingcart.entity.Cart;
import com.nusiss.shoppingcart.entity.CartItem;
import com.nusiss.shoppingcart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 购物车商品项数据访问层
 * @author SpringCA Team
 */
@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    
    /**
     * 根据购物车查找所有商品项
     * @param cart 购物车对象
     * @return 商品项列表
     */
    List<CartItem> findByCart(Cart cart);
    
    /**
     * 根据购物车ID查找所有商品项
     * @param cartId 购物车ID
     * @return 商品项列表
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId")
    List<CartItem> findByCartId(@Param("cartId") Long cartId);
    
    /**
     * 根据购物车和商品查找商品项
     * @param cart 购物车对象
     * @param product 商品对象
     * @return 商品项对象
     */
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
    
    /**
     * 根据购物车ID和商品ID查找商品项
     * @param cartId 购物车ID
     * @param productId 商品ID
     * @return 商品项对象
     */
    @Query("SELECT ci FROM CartItem ci WHERE ci.cart.id = :cartId AND ci.product.id = :productId")
    Optional<CartItem> findByCartIdAndProductId(@Param("cartId") Long cartId, @Param("productId") Long productId);
    
    /**
     * 检查购物车中是否包含指定商品
     * @param cart 购物车对象
     * @param product 商品对象
     * @return 是否存在
     */
    boolean existsByCartAndProduct(Cart cart, Product product);
    
    /**
     * 根据购物车删除所有商品项
     * @param cart 购物车对象
     */
    void deleteByCart(Cart cart);
    
    /**
     * 根据购物车ID删除所有商品项
     * @param cartId 购物车ID
     */
    @Query("DELETE FROM CartItem ci WHERE ci.cart.id = :cartId")
    void deleteByCartId(@Param("cartId") Long cartId);
    
    /**
     * 统计购物车中的商品种类数量
     * @param cart 购物车对象
     * @return 商品种类数量
     */
    @Query("SELECT COUNT(ci) FROM CartItem ci WHERE ci.cart = :cart")
    Long countByCart(@Param("cart") Cart cart);
    
    /**
     * 统计购物车中的商品总数量
     * @param cart 购物车对象
     * @return 商品总数量
     */
    @Query("SELECT SUM(ci.quantity) FROM CartItem ci WHERE ci.cart = :cart")
    Integer sumQuantityByCart(@Param("cart") Cart cart);
    
    /**
     * 查找包含指定商品的所有购物车项
     * @param product 商品对象
     * @return 购物车项列表
     */
    List<CartItem> findByProduct(Product product);
}