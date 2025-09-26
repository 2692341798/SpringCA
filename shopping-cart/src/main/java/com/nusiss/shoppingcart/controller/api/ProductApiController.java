package com.nusiss.shoppingcart.controller.api;

import com.nusiss.shoppingcart.entity.Product;
import com.nusiss.shoppingcart.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 商品REST API控制器
 * 为React前端提供商品相关的API接口
 * @author SpringCA Team
 */
@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"})
public class ProductApiController {
    
    private static final Logger log = LoggerFactory.getLogger(ProductApiController.class);
    
    private final ProductService productService;
    
    public ProductApiController(ProductService productService) {
        this.productService = productService;
    }
    
    /**
     * 获取商品列表（分页）
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "") String q,
            @RequestParam(defaultValue = "") String category,
            @RequestParam(defaultValue = "") String brand,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        try {
            Page<Product> productPage;
            
            // 根据不同条件进行查询
            if (!q.trim().isEmpty()) {
                productPage = productService.searchProducts(q, page, size);
            } else if (!category.trim().isEmpty()) {
                productPage = productService.getProductsByCategory(category, page, size);
            } else if (!brand.trim().isEmpty()) {
                productPage = productService.getProductsByBrand(brand, page, size);
            } else {
                productPage = productService.getAllProducts(page, size, sortBy, sortDir);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", productPage.getContent());
            response.put("pagination", Map.of(
                "currentPage", productPage.getNumber(),
                "totalPages", productPage.getTotalPages(),
                "totalElements", productPage.getTotalElements(),
                "size", productPage.getSize(),
                "hasNext", productPage.hasNext(),
                "hasPrevious", productPage.hasPrevious()
            ));
            response.put("filters", Map.of(
                "query", q,
                "category", category,
                "brand", brand,
                "sortBy", sortBy,
                "sortDir", sortDir
            ));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取商品列表失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取商品列表失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getProduct(@PathVariable Long id) {
        try {
            Optional<Product> productOpt = productService.getProductById(id);
            
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                
                // 获取相关商品推荐
                List<Product> relatedProducts = productService.getRelatedProducts(product, 4);
                
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", product);
                response.put("relatedProducts", relatedProducts);
                
                return ResponseEntity.ok(response);
            } else {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "商品不存在");
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            log.error("获取商品详情失败，商品ID: {}", id, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取商品详情失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 获取商品分类列表
     */
    @GetMapping("/categories")
    public ResponseEntity<Map<String, Object>> getCategories() {
        try {
            List<String> categories = productService.getAllCategories();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", categories);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取商品分类失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取商品分类失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 获取商品品牌列表
     */
    @GetMapping("/brands")
    public ResponseEntity<Map<String, Object>> getBrands() {
        try {
            List<String> brands = productService.getAllBrands();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", brands);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取商品品牌失败", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取商品品牌失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
    /**
     * 搜索商品建议
     */
    @GetMapping("/suggestions")
    public ResponseEntity<Map<String, Object>> getSearchSuggestions(@RequestParam String q) {
        try {
            if (q.trim().length() < 2) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", true);
                response.put("data", List.of());
                return ResponseEntity.ok(response);
            }
            
            List<Product> suggestions = productService.getSearchSuggestions(q, 5);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", suggestions.stream().map(product -> Map.of(
                "id", product.getId(),
                "name", product.getName(),
                "category", product.getCategory(),
                "price", product.getPrice()
            )).toList());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("获取搜索建议失败，查询词: {}", q, e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "获取搜索建议失败：" + e.getMessage());
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}