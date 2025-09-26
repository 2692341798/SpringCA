package com.nusiss.shoppingcart.config;

import com.nusiss.shoppingcart.entity.Product;
import com.nusiss.shoppingcart.entity.User;
import com.nusiss.shoppingcart.repository.ProductRepository;
import com.nusiss.shoppingcart.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 数据初始化器
 * 在应用启动时初始化测试数据
 * @author SpringCA Team
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);
    
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    public DataInitializer(UserRepository userRepository, ProductRepository productRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }
    
    @Override
    public void run(String... args) throws Exception {
        log.info("开始初始化数据...");
        
        // 初始化用户数据
        initializeUsers();
        
        // 初始化商品数据
        initializeProducts();
        
        log.info("数据初始化完成！");
    }
    
    /**
     * 初始化用户数据
     */
    private void initializeUsers() {
        if (userRepository.count() == 0) {
            log.info("初始化用户数据...");
            
            // 创建管理员用户
            User admin = new User("admin", "admin123", "admin@example.com");
            admin.setFirstName("管理员");
            admin.setLastName("系统");
            admin.setPhone("13800138000");
            admin.setAddress("新加坡国立大学");
            
            // 创建测试用户1
            User john = new User("john", "john123", "john@example.com");
            john.setFirstName("John");
            john.setLastName("Doe");
            john.setPhone("13800138001");
            john.setAddress("123 Orchard Road, Singapore");
            
            // 创建测试用户2
            User alice = new User("alice", "alice123", "alice@example.com");
            alice.setFirstName("Alice");
            alice.setLastName("Smith");
            alice.setPhone("13800138002");
            alice.setAddress("456 Marina Bay, Singapore");
            
            // 创建测试用户3
            User bob = new User("bob", "bob123", "bob@example.com");
            bob.setFirstName("Bob");
            bob.setLastName("Johnson");
            bob.setPhone("13800138003");
            bob.setAddress("789 Sentosa Island, Singapore");
            
            List<User> users = Arrays.asList(admin, john, alice, bob);
            userRepository.saveAll(users);
            log.info("已创建 {} 个测试用户", users.size());
        } else {
            log.info("用户数据已存在，跳过初始化");
        }
    }
    
    /**
     * 初始化商品数据
     */
    private void initializeProducts() {
        if (productRepository.count() == 0) {
            log.info("初始化商品数据...");
            
            // 创建商品列表
            List<Product> products = new ArrayList<>();
            
            // 电子产品
            Product iphone = new Product("iPhone 15 Pro", "苹果最新旗舰手机，配备A17 Pro芯片", 
                           new BigDecimal("1299.00"), 50, "电子产品");
            iphone.setBrand("Apple");
            iphone.setImageUrl("https://via.placeholder.com/300x200?text=iPhone+15+Pro");
            iphone.setRating(new BigDecimal("4.8"));
            iphone.setReviewCount(256);
            products.add(iphone);
            
            Product samsung = new Product("Samsung Galaxy S24", "三星旗舰智能手机，AI摄影专家", 
                           new BigDecimal("1199.00"), 30, "电子产品");
            samsung.setBrand("Samsung");
            samsung.setImageUrl("https://via.placeholder.com/300x200?text=Galaxy+S24");
            samsung.setRating(new BigDecimal("4.6"));
            samsung.setReviewCount(189);
            products.add(samsung);
            
            Product macbook = new Product("MacBook Air M3", "苹果笔记本电脑，M3芯片强劲性能", 
                           new BigDecimal("1599.00"), 25, "电子产品");
            macbook.setBrand("Apple");
            macbook.setImageUrl("https://via.placeholder.com/300x200?text=MacBook+Air+M3");
            macbook.setRating(new BigDecimal("4.9"));
            macbook.setReviewCount(342);
            products.add(macbook);
            
            // 服装
            Product nike = new Product("Nike Air Max 270", "耐克经典运动鞋，舒适透气", 
                           new BigDecimal("159.00"), 100, "服装");
            nike.setBrand("Nike");
            nike.setImageUrl("https://via.placeholder.com/300x200?text=Nike+Air+Max");
            nike.setRating(new BigDecimal("4.4"));
            nike.setReviewCount(567);
            products.add(nike);
            
            Product adidas = new Product("Adidas Ultraboost 22", "阿迪达斯跑步鞋，能量回弹科技", 
                           new BigDecimal("179.00"), 80, "服装");
            adidas.setBrand("Adidas");
            adidas.setImageUrl("https://via.placeholder.com/300x200?text=Adidas+Ultraboost");
            adidas.setRating(new BigDecimal("4.6"));
            adidas.setReviewCount(423);
            products.add(adidas);
            
            // 家居用品
            Product ikea = new Product("IKEA 宜家书桌", "简约现代书桌，适合家庭办公", 
                           new BigDecimal("199.00"), 40, "家居用品");
            ikea.setBrand("IKEA");
            ikea.setImageUrl("https://via.placeholder.com/300x200?text=IKEA+Desk");
            ikea.setRating(new BigDecimal("4.1"));
            ikea.setReviewCount(234);
            products.add(ikea);
            
            Product dyson = new Product("Dyson V15吸尘器", "戴森无线吸尘器，强劲吸力", 
                           new BigDecimal("699.00"), 15, "家居用品");
            dyson.setBrand("Dyson");
            dyson.setImageUrl("https://via.placeholder.com/300x200?text=Dyson+V15");
            dyson.setRating(new BigDecimal("4.8"));
            dyson.setReviewCount(167);
            products.add(dyson);
            
            // 图书
            Product springBook = new Product("《Spring Boot实战》", "Spring Boot开发指南，程序员必读", 
                           new BigDecimal("59.00"), 120, "图书");
            springBook.setBrand("人民邮电出版社");
            springBook.setImageUrl("https://via.placeholder.com/300x200?text=Spring+Boot+Book");
            springBook.setRating(new BigDecimal("4.7"));
            springBook.setReviewCount(89);
            products.add(springBook);
            
            Product javaBook = new Product("《Java核心技术》", "Java编程经典教材，权威参考", 
                           new BigDecimal("89.00"), 90, "图书");
            javaBook.setBrand("机械工业出版社");
            javaBook.setImageUrl("https://via.placeholder.com/300x200?text=Java+Core+Book");
            javaBook.setRating(new BigDecimal("4.9"));
            javaBook.setReviewCount(156);
            products.add(javaBook);
            
            // 运动用品
            Product tennis = new Product("Wilson 威尔胜网球拍", "专业网球拍，适合中高级选手", 
                           new BigDecimal("299.00"), 25, "运动用品");
            tennis.setBrand("Wilson");
            tennis.setImageUrl("https://via.placeholder.com/300x200?text=Wilson+Tennis");
            tennis.setRating(new BigDecimal("4.5"));
            tennis.setReviewCount(78);
            products.add(tennis);
            
            productRepository.saveAll(products);
            log.info("已创建 {} 个测试商品", products.size());
        } else {
            log.info("商品数据已存在，跳过初始化");
        }
    }
}