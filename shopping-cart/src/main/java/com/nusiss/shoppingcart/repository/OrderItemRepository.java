package com.nusiss.shoppingcart.repository;

import com.nusiss.shoppingcart.entity.Order;
import com.nusiss.shoppingcart.entity.OrderItem;
import com.nusiss.shoppingcart.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单商品项数据访问层
 * @author SpringCA Team
 */
@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    
    /**
     * 根据订单查找所有商品项
     * @param order 订单对象
     * @return 商品项列表
     */
    List<OrderItem> findByOrder(Order order);
    
    /**
     * 根据订单ID查找所有商品项
     * @param orderId 订单ID
     * @return 商品项列表
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.order.id = :orderId")
    List<OrderItem> findByOrderId(@Param("orderId") Long orderId);
    
    /**
     * 根据商品查找所有订单项
     * @param product 商品对象
     * @return 订单项列表
     */
    List<OrderItem> findByProduct(Product product);
    
    /**
     * 根据商品ID查找所有订单项
     * @param productId 商品ID
     * @return 订单项列表
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product.id = :productId")
    List<OrderItem> findByProductId(@Param("productId") Long productId);
    
    /**
     * 统计订单中的商品种类数量
     * @param order 订单对象
     * @return 商品种类数量
     */
    @Query("SELECT COUNT(oi) FROM OrderItem oi WHERE oi.order = :order")
    Long countByOrder(@Param("order") Order order);
    
    /**
     * 统计订单中的商品总数量
     * @param order 订单对象
     * @return 商品总数量
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.order = :order")
    Integer sumQuantityByOrder(@Param("order") Order order);
    
    /**
     * 查找指定时间范围内某商品的销售记录
     * @param product 商品对象
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单项列表
     */
    @Query("SELECT oi FROM OrderItem oi WHERE oi.product = :product AND oi.createdAt BETWEEN :startTime AND :endTime")
    List<OrderItem> findByProductAndCreatedAtBetween(@Param("product") Product product, 
                                                     @Param("startTime") LocalDateTime startTime, 
                                                     @Param("endTime") LocalDateTime endTime);
    
    /**
     * 统计指定时间范围内某商品的销售数量
     * @param product 商品对象
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 销售数量
     */
    @Query("SELECT SUM(oi.quantity) FROM OrderItem oi WHERE oi.product = :product AND oi.createdAt BETWEEN :startTime AND :endTime")
    Integer sumQuantityByProductAndCreatedAtBetween(@Param("product") Product product, 
                                                    @Param("startTime") LocalDateTime startTime, 
                                                    @Param("endTime") LocalDateTime endTime);
    
    /**
     * 查找热销商品（按销量排序）
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param limit 限制数量
     * @return 商品ID和销量列表
     */
    @Query("SELECT oi.product.id, SUM(oi.quantity) as totalSold " +
           "FROM OrderItem oi " +
           "WHERE oi.createdAt BETWEEN :startTime AND :endTime " +
           "GROUP BY oi.product.id " +
           "ORDER BY totalSold DESC")
    List<Object[]> findTopSellingProducts(@Param("startTime") LocalDateTime startTime, 
                                          @Param("endTime") LocalDateTime endTime);
    
    /**
     * 根据订单删除所有商品项
     * @param order 订单对象
     */
    void deleteByOrder(Order order);
}