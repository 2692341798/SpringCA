package com.nusiss.shoppingcart.controller;

import com.nusiss.shoppingcart.entity.Product;
import com.nusiss.shoppingcart.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

/**
 * 商品控制器
 * @author SpringCA Team
 */
@Controller
public class ProductController {
    
    private static final Logger log = LoggerFactory.getLogger(ProductController.class);
    
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    /**
     * 商品列表页面（支持搜索和分页）
     */
    @GetMapping("/products")
    public String listProducts(@RequestParam(defaultValue = "") String q,
                              @RequestParam(defaultValue = "") String category,
                              @RequestParam(defaultValue = "") String brand,
                              @RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "12") int size,
                              @RequestParam(defaultValue = "name") String sortBy,
                              @RequestParam(defaultValue = "asc") String sortDir,
                              Model model) {
        
        try {
            Page<Product> productPage;
            
            // 根据不同条件进行查询
            if (!q.trim().isEmpty()) {
                productPage = productService.searchProducts(q, page, size);
                model.addAttribute("searchType", "keyword");
                model.addAttribute("searchValue", q);
            } else if (!category.trim().isEmpty()) {
                productPage = productService.getProductsByCategory(category, page, size);
                model.addAttribute("searchType", "category");
                model.addAttribute("searchValue", category);
            } else if (!brand.trim().isEmpty()) {
                productPage = productService.getProductsByBrand(brand, page, size);
                model.addAttribute("searchType", "brand");
                model.addAttribute("searchValue", brand);
            } else {
                productPage = productService.getAllProducts(page, size, sortBy, sortDir);
                model.addAttribute("searchType", "all");
            }
            
            // 添加分页和商品数据到模型
            model.addAttribute("products", productPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalElements", productPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("sortDir", sortDir);
            
            // 添加搜索参数
            model.addAttribute("q", q);
            model.addAttribute("category", category);
            model.addAttribute("brand", brand);
            
            // 添加分类和品牌列表用于筛选
            model.addAttribute("categories", productService.getAllCategories());
            model.addAttribute("brands", productService.getAllBrands());
            
            // 分页导航信息
            model.addAttribute("hasPrevious", productPage.hasPrevious());
            model.addAttribute("hasNext", productPage.hasNext());
            model.addAttribute("isFirst", productPage.isFirst());
            model.addAttribute("isLast", productPage.isLast());
            
        } catch (Exception e) {
            log.error("获取商品列表时发生错误：{}", e.getMessage());
            model.addAttribute("products", Page.empty().getContent());
            model.addAttribute("errorMessage", "获取商品列表失败，请稍后重试");
        }
        
        return "products";
    }
    
    /**
     * 商品详情页面
     */
    @GetMapping("/products/{id}")
    public String productDetail(@PathVariable Long id, Model model) {
        try {
            Optional<Product> productOpt = productService.getProductById(id);
            
            if (productOpt.isPresent()) {
                Product product = productOpt.get();
                model.addAttribute("product", product);
                
                // 获取相关商品（同分类的其他商品）
                if (product.getCategory() != null) {
                    Page<Product> relatedProducts = productService.getProductsByCategory(
                        product.getCategory(), 0, 4);
                    model.addAttribute("relatedProducts", 
                        relatedProducts.getContent().stream()
                            .filter(p -> !p.getId().equals(id))
                            .limit(4)
                            .toList());
                }
                
                return "product-detail";
            } else {
                model.addAttribute("errorMessage", "商品不存在");
                return "redirect:/products";
            }
        } catch (Exception e) {
            log.error("获取商品详情时发生错误：{}", e.getMessage());
            model.addAttribute("errorMessage", "获取商品详情失败");
            return "redirect:/products";
        }
    }
    
    /**
     * 热门商品页面
     */
    @GetMapping("/products/popular")
    public String popularProducts(@RequestParam(defaultValue = "0") int page,
                                 @RequestParam(defaultValue = "12") int size,
                                 Model model) {
        try {
            Page<Product> productPage = productService.getPopularProducts(page, size);
            
            model.addAttribute("products", productPage.getContent());
            model.addAttribute("currentPage", page);
            model.addAttribute("totalPages", productPage.getTotalPages());
            model.addAttribute("totalElements", productPage.getTotalElements());
            model.addAttribute("size", size);
            model.addAttribute("pageTitle", "热门商品");
            
            return "products";
        } catch (Exception e) {
            log.error("获取热门商品时发生错误：{}", e.getMessage());
            return "redirect:/products";
        }
    }
}