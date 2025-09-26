package com.nusiss.shoppingcart.repository;

import com.nusiss.shoppingcart.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

/**
 * 商品数据访问层
 * @author SpringCA Team
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    /**
     * 查找所有活跃商品（分页）
     * @param pageable 分页参数
     * @return 商品分页结果
     */
    Page<Product> findByActiveTrue(Pageable pageable);
    
    /**
     * 根据商品名称搜索（模糊查询，分页）
     * @param name 商品名称
     * @param pageable 分页参数
     * @return 商品分页结果
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.name LIKE %:name%")
    Page<Product> findByNameContainingIgnoreCaseAndActiveTrue(@Param("name") String name, Pageable pageable);
    
    /**
     * 根据分类查找商品（分页）
     * @param category 分类
     * @param pageable 分页参数
     * @return 商品分页结果
     */
    Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);
    
    /**
     * 根据品牌查找商品（分页）
     * @param brand 品牌
     * @param pageable 分页参数
     * @return 商品分页结果
     */
    Page<Product> findByBrandAndActiveTrue(String brand, Pageable pageable);
    
    /**
     * 根据价格范围查找商品（分页）
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param pageable 分页参数
     * @return 商品分页结果
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.price BETWEEN :minPrice AND :maxPrice")
    Page<Product> findByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                   @Param("maxPrice") BigDecimal maxPrice, 
                                   Pageable pageable);
    
    /**
     * 综合搜索：根据关键词搜索商品名称、描述、分类（分页）
     * @param keyword 搜索关键词
     * @param pageable 分页参数
     * @return 商品分页结果
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND " +
           "(p.name LIKE %:keyword% OR p.description LIKE %:keyword% OR p.category LIKE %:keyword%)")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 查找库存不足的商品
     * @param threshold 库存阈值
     * @return 库存不足的商品列表
     */
    @Query("SELECT p FROM Product p WHERE p.active = true AND p.stock <= :threshold")
    List<Product> findLowStockProducts(@Param("threshold") Integer threshold);
    
    /**
     * 查找热门商品（按评分和评论数排序）
     * @param pageable 分页参数
     * @return 热门商品分页结果
     */
    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.rating DESC, p.reviewCount DESC")
    Page<Product> findPopularProducts(Pageable pageable);
    
    /**
     * 获取所有商品分类
     * @return 分类列表
     */
    @Query("SELECT DISTINCT p.category FROM Product p WHERE p.active = true AND p.category IS NOT NULL")
    List<String> findAllCategories();
    
    /**
     * 获取所有商品品牌
     * @return 品牌列表
     */
    @Query("SELECT DISTINCT p.brand FROM Product p WHERE p.active = true AND p.brand IS NOT NULL")
    List<String> findAllBrands();
    
    /**
     * 查找库存低于阈值的商品
     * @param threshold 库存阈值
     * @return 库存不足的商品列表
     */
    List<Product> findByStockLessThanAndActiveTrue(Integer threshold);
    
    /**
     * 根据分类查找商品，排除指定ID的商品
     * @param category 分类
     * @param excludeId 要排除的商品ID
     * @param pageable 分页参数
     * @return 商品分页结果
     */
    Page<Product> findByCategoryAndActiveTrueAndIdNot(String category, Long excludeId, Pageable pageable);
    
    /**
     * 根据品牌查找商品，排除指定ID列表的商品
     * @param brand 品牌
     * @param excludeIds 要排除的商品ID列表
     * @param pageable 分页参数
     * @return 商品分页结果
     */
    Page<Product> findByBrandAndActiveTrueAndIdNotIn(String brand, List<Long> excludeIds, Pageable pageable);
    
    /**
     * 查找活跃商品，排除指定ID列表的商品
     * @param excludeIds 要排除的商品ID列表
     * @param pageable 分页参数
     * @return 商品分页结果
     */
    Page<Product> findByActiveTrueAndIdNotIn(List<Long> excludeIds, Pageable pageable);
}