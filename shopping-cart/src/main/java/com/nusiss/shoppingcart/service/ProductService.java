package com.nusiss.shoppingcart.service;

import com.nusiss.shoppingcart.entity.Product;
import com.nusiss.shoppingcart.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * 商品业务逻辑层
 * @author SpringCA Team
 */
@Service
@Transactional(readOnly = true)
public class ProductService {
    
    private static final Logger log = LoggerFactory.getLogger(ProductService.class);
    
    private final ProductRepository productRepository;
    
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }
    
    /**
     * 获取所有商品（分页）
     * @param page 页码（从0开始）
     * @param size 每页大小
     * @param sortBy 排序字段
     * @param sortDir 排序方向
     * @return 商品分页结果
     */
    public Page<Product> getAllProducts(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                   Sort.by(sortBy).descending() : 
                   Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return productRepository.findByActiveTrue(pageable);
    }
    
    /**
     * 根据ID获取商品详情
     * @param id 商品ID
     * @return 商品对象
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    /**
     * 搜索商品
     * @param keyword 搜索关键词
     * @param page 页码
     * @param size 每页大小
     * @return 搜索结果分页
     */
    public Page<Product> searchProducts(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        if (keyword == null || keyword.trim().isEmpty()) {
            return productRepository.findByActiveTrue(pageable);
        }
        return productRepository.searchProducts(keyword.trim(), pageable);
    }
    
    /**
     * 根据分类获取商品
     * @param category 分类
     * @param page 页码
     * @param size 每页大小
     * @return 商品分页结果
     */
    public Page<Product> getProductsByCategory(String category, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findByCategoryAndActiveTrue(category, pageable);
    }
    
    /**
     * 根据品牌获取商品
     * @param brand 品牌
     * @param page 页码
     * @param size 每页大小
     * @return 商品分页结果
     */
    public Page<Product> getProductsByBrand(String brand, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("name").ascending());
        return productRepository.findByBrandAndActiveTrue(brand, pageable);
    }
    
    /**
     * 根据价格范围获取商品
     * @param minPrice 最低价格
     * @param maxPrice 最高价格
     * @param page 页码
     * @param size 每页大小
     * @return 商品分页结果
     */
    public Page<Product> getProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("price").ascending());
        return productRepository.findByPriceRange(minPrice, maxPrice, pageable);
    }
    
    /**
     * 获取热门商品
     * @param page 页码
     * @param size 每页大小
     * @return 热门商品分页结果
     */
    public Page<Product> getPopularProducts(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return productRepository.findPopularProducts(pageable);
    }
    
    /**
     * 获取所有分类
     * @return 分类列表
     */
    public List<String> getAllCategories() {
        return productRepository.findAllCategories();
    }
    
    /**
     * 获取所有品牌
     * @return 品牌列表
     */
    public List<String> getAllBrands() {
        return productRepository.findAllBrands();
    }
    
    /**
     * 检查商品库存
     * @param productId 商品ID
     * @param quantity 需要的数量
     * @return 是否有足够库存
     */
    public boolean checkStock(Long productId, int quantity) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            return product.hasStock(quantity);
        }
        return false;
    }
    
    /**
     * 减少商品库存
     * @param productId 商品ID
     * @param quantity 减少的数量
     * @return 是否成功
     */
    @Transactional
    public boolean reduceStock(Long productId, int quantity) {
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.reduceStock(quantity);
                productRepository.save(product);
                log.info("商品 {} 库存减少 {} 个，当前库存：{}", product.getName(), quantity, product.getStock());
                return true;
            }
        } catch (Exception e) {
            log.error("减少商品库存失败：{}", e.getMessage());
        }
        return false;
    }
    
    /**
     * 增加商品库存
     * @param productId 商品ID
     * @param quantity 增加的数量
     * @return 是否成功
     */
    @Transactional
    public boolean addStock(Long productId, int quantity) {
        try {
            Optional<Product> productOpt = productRepository.findById(productId);
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                product.addStock(quantity);
                productRepository.save(product);
                log.info("商品 {} 库存增加 {} 个，当前库存：{}", product.getName(), quantity, product.getStock());
                return true;
            }
        } catch (Exception e) {
            log.error("增加商品库存失败：{}", e.getMessage());
        }
        return false;
    }
    
    /**
     * 获取库存不足的商品
     * @param threshold 库存阈值
     * @return 库存不足的商品列表
     */
    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findByStockLessThanAndActiveTrue(threshold);
    }
    
    /**
     * 获取相关商品推荐
     * @param product 当前商品
     * @param limit 推荐数量限制
     * @return 相关商品列表
     */
    public List<Product> getRelatedProducts(Product product, int limit) {
        try {
            // 优先推荐同分类的商品
            List<Product> relatedProducts = productRepository.findByCategoryAndActiveTrueAndIdNot(
                product.getCategory(), product.getId(), PageRequest.of(0, limit)
            ).getContent();
            
            // 如果同分类商品不足，补充同品牌的商品
            if (relatedProducts.size() < limit) {
                int remaining = limit - relatedProducts.size();
                List<Product> brandProducts = productRepository.findByBrandAndActiveTrueAndIdNotIn(
                    product.getBrand(), 
                    relatedProducts.stream().map(Product::getId).toList(),
                    PageRequest.of(0, remaining)
                ).getContent();
                relatedProducts.addAll(brandProducts);
            }
            
            // 如果还不足，补充其他热门商品
            if (relatedProducts.size() < limit) {
                int remaining = limit - relatedProducts.size();
                List<Product> popularProducts = productRepository.findByActiveTrueAndIdNotIn(
                    relatedProducts.stream().map(Product::getId).toList(),
                    PageRequest.of(0, remaining, Sort.by("reviewCount").descending())
                ).getContent();
                relatedProducts.addAll(popularProducts);
            }
            
            return relatedProducts.subList(0, Math.min(relatedProducts.size(), limit));
            
        } catch (Exception e) {
            log.error("获取相关商品推荐失败，商品ID: {}", product.getId(), e);
            return List.of();
        }
    }
    
    /**
     * 获取搜索建议
     * @param query 搜索查询词
     * @param limit 建议数量限制
     * @return 搜索建议列表
     */
    public List<Product> getSearchSuggestions(String query, int limit) {
        try {
            if (query == null || query.trim().length() < 2) {
                return List.of();
            }
            
            String keyword = query.trim();
            Pageable pageable = PageRequest.of(0, limit, Sort.by("reviewCount").descending());
            
            // 搜索商品名称包含关键词的商品
            return productRepository.searchProducts(keyword, pageable).getContent();
            
        } catch (Exception e) {
            log.error("获取搜索建议失败，查询词: {}", query, e);
            return List.of();
        }
    }
}