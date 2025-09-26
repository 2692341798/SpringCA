package com.nusiss.shoppingcart.repository;

import com.nusiss.shoppingcart.entity.Cart;
import com.nusiss.shoppingcart.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 购物车数据访问层
 * @author SpringCA Team
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    /**
     * 根据用户查找活跃的购物车
     * @param user 用户对象
     * @return 购物车对象
     */
    Optional<Cart> findByUserAndActiveTrue(User user);
    
    /**
     * 根据用户ID查找活跃的购物车
     * @param userId 用户ID
     * @return 购物车对象
     */
    @Query("SELECT c FROM Cart c WHERE c.user.id = :userId AND c.active = true")
    Optional<Cart> findByUserIdAndActiveTrue(@Param("userId") Long userId);
    
    /**
     * 根据用户查找购物车（包括非活跃的）
     * @param user 用户对象
     * @return 购物车对象
     */
    Optional<Cart> findByUser(User user);
    
    /**
     * 检查用户是否有活跃的购物车
     * @param user 用户对象
     * @return 是否存在
     */
    boolean existsByUserAndActiveTrue(User user);
    
    /**
     * 根据用户ID检查是否有活跃的购物车
     * @param userId 用户ID
     * @return 是否存在
     */
    @Query("SELECT COUNT(c) > 0 FROM Cart c WHERE c.user.id = :userId AND c.active = true")
    boolean existsByUserIdAndActiveTrue(@Param("userId") Long userId);
    
    /**
     * 删除用户的所有非活跃购物车
     * @param user 用户对象
     */
    void deleteByUserAndActiveFalse(User user);
}