package com.nusiss.shoppingcart.repository;

import com.nusiss.shoppingcart.entity.Order;
import com.nusiss.shoppingcart.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 订单数据访问层
 * @author SpringCA Team
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    /**
     * 根据订单号查找订单
     * @param orderNumber 订单号
     * @return 订单对象
     */
    Optional<Order> findByOrderNumber(String orderNumber);
    
    /**
     * 根据用户查找所有订单（分页）
     * @param user 用户对象
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByUser(User user, Pageable pageable);
    
    /**
     * 根据用户ID查找所有订单（分页）
     * @param userId 用户ID
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.createdAt DESC")
    Page<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 根据用户和订单状态查找订单（分页）
     * @param user 用户对象
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByUserAndStatus(User user, Order.OrderStatus status, Pageable pageable);
    
    /**
     * 根据用户ID和订单状态查找订单（分页）
     * @param userId 用户ID
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status ORDER BY o.createdAt DESC")
    Page<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Order.OrderStatus status, Pageable pageable);
    
    /**
     * 根据订单状态查找所有订单（分页）
     * @param status 订单状态
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    Page<Order> findByStatus(Order.OrderStatus status, Pageable pageable);
    
    /**
     * 查找指定时间范围内的订单
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startTime AND :endTime ORDER BY o.createdAt DESC")
    Page<Order> findByCreatedAtBetween(@Param("startTime") LocalDateTime startTime, 
                                       @Param("endTime") LocalDateTime endTime, 
                                       Pageable pageable);
    
    /**
     * 查找用户在指定时间范围内的订单
     * @param user 用户对象
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param pageable 分页参数
     * @return 订单分页结果
     */
    @Query("SELECT o FROM Order o WHERE o.user = :user AND o.createdAt BETWEEN :startTime AND :endTime ORDER BY o.createdAt DESC")
    Page<Order> findByUserAndCreatedAtBetween(@Param("user") User user, 
                                              @Param("startTime") LocalDateTime startTime, 
                                              @Param("endTime") LocalDateTime endTime, 
                                              Pageable pageable);
    
    /**
     * 统计用户的订单总数
     * @param user 用户对象
     * @return 订单总数
     */
    Long countByUser(User user);
    
    /**
     * 统计指定状态的订单数量
     * @param status 订单状态
     * @return 订单数量
     */
    Long countByStatus(Order.OrderStatus status);
    
    /**
     * 查找需要自动取消的订单（超过指定时间未支付）
     * @param cutoffTime 截止时间
     * @return 订单列表
     */
    @Query("SELECT o FROM Order o WHERE o.status = 'PENDING' AND o.createdAt < :cutoffTime")
    List<Order> findPendingOrdersBeforeTime(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * 检查订单号是否存在
     * @param orderNumber 订单号
     * @return 是否存在
     */
    boolean existsByOrderNumber(String orderNumber);
}