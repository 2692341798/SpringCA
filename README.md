# SpringCA 购物车系统

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.4-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18.2.0-blue.svg)](https://reactjs.org/)
[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://openjdk.java.net/)
[![Maven](https://img.shields.io/badge/Maven-3.6+-red.svg)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

一个基于 Spring Boot 3.3.4 和 React 18.2.0 的现代化电商购物车系统，提供完整的商品浏览、购物车管理、订单处理等功能。

## 📋 目录

- [项目概述](#项目概述)
- [技术栈](#技术栈)
- [功能特性](#功能特性)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [API文档](#api文档)
- [开发指南](#开发指南)
- [部署说明](#部署说明)
- [测试账户](#测试账户)
- [开发历程](#开发历程)
- [贡献指南](#贡献指南)
- [许可证](#许可证)

## 🚀 项目概述

SpringCA 购物车系统是一个全栈电商应用，采用前后端分离架构设计。系统提供了完整的电商购物流程，从商品浏览到订单完成的全链路功能。

### 核心特性

- 🛍️ **完整的购物流程**: 商品浏览 → 加入购物车 → 下单结账 → 订单管理
- 🎨 **现代化UI设计**: 响应式设计，支持移动端和桌面端
- ⚡ **高性能**: 优化的前端资源加载和后端API响应
- 🔒 **安全可靠**: 完善的异常处理和数据验证机制
- 🔧 **易于扩展**: 模块化设计，支持功能扩展

## 🛠️ 技术栈

### 后端技术
- **框架**: Spring Boot 3.3.4
- **数据库**: H2 (开发环境) / MySQL (生产环境)
- **ORM**: Spring Data JPA + Hibernate
- **模板引擎**: Thymeleaf
- **构建工具**: Maven 3.6+
- **Java版本**: JDK 17

### 前端技术
- **框架**: React 18.2.0
- **构建工具**: Webpack 5.101.3
- **HTTP客户端**: Axios 1.6.0
- **UI框架**: Bootstrap 5.3.3
- **图标库**: Bootstrap Icons 1.11.0
- **编译器**: Babel 7.23.0

### 开发工具
- **热重载**: Spring Boot DevTools
- **前端集成**: Maven Frontend Plugin
- **代码简化**: Lombok
- **数据库控制台**: H2 Console

## ✨ 功能特性

### 🔐 用户管理
- 用户注册与登录
- 密码强度验证
- 用户信息管理
- Session 会话管理

### 🛒 商品管理
- 商品列表展示（分页、搜索、筛选）
- 商品详情页面
- 多维度筛选（分类、品牌、价格）
- 商品评分和评价展示
- 库存管理

### 🛍️ 购物车功能
- 添加商品到购物车
- 购物车商品数量管理
- 实时价格计算
- 库存验证
- 购物车持久化

### 📦 订单管理
- 订单创建和确认
- 订单状态跟踪
- 订单历史查询
- 订单详情展示
- 库存自动扣减

### 🎨 用户体验
- 响应式设计
- 现代化UI界面
- 流畅的交互动画
- 友好的错误提示
- 快速的页面加载

### 🔧 技术特性
- REST API 架构
- 前后端分离
- 全局异常处理
- 数据验证机制
- 事务管理
- 性能优化

## 📁 项目结构

```
SpringCA/
├── shopping-cart/                    # 主应用模块
│   ├── src/main/
│   │   ├── java/com/nusiss/shoppingcart/
│   │   │   ├── ShoppingCartApplication.java    # 应用启动类
│   │   │   ├── controller/                     # 控制器层
│   │   │   │   ├── AuthController.java         # 用户认证
│   │   │   │   ├── CartController.java         # 购物车管理
│   │   │   │   ├── HomeController.java         # 首页控制
│   │   │   │   ├── OrderController.java        # 订单管理
│   │   │   │   ├── ProductController.java      # 商品管理
│   │   │   │   ├── ReactController.java        # React页面
│   │   │   │   └── api/                        # REST API
│   │   │   │       ├── CartApiController.java  # 购物车API
│   │   │   │       └── ProductApiController.java # 商品API
│   │   │   ├── entity/                         # 实体类
│   │   │   │   ├── User.java                   # 用户实体
│   │   │   │   ├── Product.java                # 商品实体
│   │   │   │   ├── Cart.java                   # 购物车实体
│   │   │   │   ├── CartItem.java               # 购物车项
│   │   │   │   ├── Order.java                  # 订单实体
│   │   │   │   └── OrderItem.java              # 订单项
│   │   │   ├── repository/                     # 数据访问层
│   │   │   ├── service/                        # 业务逻辑层
│   │   │   ├── dto/                           # 数据传输对象
│   │   │   ├── exception/                      # 异常处理
│   │   │   └── config/                        # 配置类
│   │   ├── frontend/                          # React前端
│   │   │   ├── package.json                   # 前端依赖
│   │   │   ├── webpack.config.js              # 构建配置
│   │   │   ├── public/                        # 静态资源
│   │   │   └── src/                           # React源码
│   │   │       ├── components/                # React组件
│   │   │       ├── services/                  # API服务
│   │   │       └── utils/                     # 工具函数
│   │   └── resources/
│   │       ├── application.properties          # 主配置
│   │       ├── application-dev.properties      # 开发环境
│   │       ├── application-prod.properties     # 生产环境
│   │       └── templates/                     # Thymeleaf模板
│   └── pom.xml                               # Maven配置
├── 开发日志-1.md                              # Sprint 1 开发日志
├── 开发日志-2.md                              # Sprint 2 开发日志
├── 开发日志-3.md                              # Sprint 3 开发日志
├── 开发日志-4.md                              # Sprint 4 开发日志
└── README.md                                 # 项目说明文档
```

## 🚀 快速开始

### 环境要求

- **JDK**: 17 或更高版本
- **Maven**: 3.6 或更高版本
- **Node.js**: 18.18.0 或更高版本 (自动安装)
- **npm**: 9.8.1 或更高版本 (自动安装)

### 安装步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd SpringCA
   ```

2. **进入主模块**
   ```bash
   cd shopping-cart
   ```

3. **构建项目** (包含前端构建)
   ```bash
   mvn clean install
   ```

4. **启动应用**
   ```bash
   mvn spring-boot:run
   ```

5. **访问应用**
   - 主页: http://localhost:8080
   - H2控制台: http://localhost:8080/h2-console
   - React页面: http://localhost:8080/react

### 开发模式

**后端开发**:
```bash
mvn spring-boot:run
```

**前端开发** (独立开发服务器):
```bash
cd src/main/frontend
npm start
```

## 📚 API文档

### 商品API

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/products` | 获取商品列表 |
| GET | `/api/products/{id}` | 获取商品详情 |
| GET | `/api/products/categories` | 获取商品分类 |
| GET | `/api/products/brands` | 获取商品品牌 |

### 购物车API

| 方法 | 路径 | 描述 |
|------|------|------|
| GET | `/api/cart` | 获取购物车信息 |
| POST | `/api/cart/add` | 添加商品到购物车 |
| PUT | `/api/cart/update` | 更新购物车商品数量 |
| DELETE | `/api/cart/remove/{id}` | 删除购物车商品 |
| DELETE | `/api/cart/clear` | 清空购物车 |
| GET | `/api/cart/count` | 获取购物车数量 |

### API响应格式

```json
{
  "success": true,
  "data": {...},
  "message": "操作成功",
  "timestamp": 1727356800000,
  "pagination": {
    "page": 0,
    "size": 10,
    "totalElements": 100,
    "totalPages": 10
  }
}
```

## 🔧 开发指南

### 数据库配置

**开发环境** (H2内存数据库):
```properties
spring.datasource.url=jdbc:h2:mem:shopdb
spring.datasource.username=sa
spring.datasource.password=
spring.h2.console.enabled=true
```

**生产环境** (MySQL):
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/shopping_cart
spring.datasource.username=your_username
spring.datasource.password=your_password
```

### 前端开发

**组件开发**:
```javascript
// src/components/ProductList.js
import React, { useState, useEffect } from 'react';
import { getProducts } from '../services/api';

const ProductList = () => {
  const [products, setProducts] = useState([]);
  
  useEffect(() => {
    loadProducts();
  }, []);
  
  const loadProducts = async () => {
    try {
      const response = await getProducts();
      setProducts(response.data);
    } catch (error) {
      console.error('加载商品失败:', error);
    }
  };
  
  return (
    <div className="product-list">
      {/* 组件内容 */}
    </div>
  );
};

export default ProductList;
```

**API服务**:
```javascript
// src/services/api.js
import axios from 'axios';

const api = axios.create({
  baseURL: '/api',
  timeout: 10000
});

export const getProducts = (params) => api.get('/products', { params });
export const addToCart = (productId, quantity) => 
  api.post('/cart/add', { productId, quantity });
```

### 后端开发

**实体类示例**:
```java
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;
    
    // getters and setters...
}
```

**服务类示例**:
```java
@Service
@Transactional
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    public Page<Product> getProducts(Pageable pageable) {
        return productRepository.findByActiveTrue(pageable);
    }
    
    public Product getProductById(Long id) {
        return productRepository.findById(id)
            .orElseThrow(() -> new BusinessException("商品不存在"));
    }
}
```

## 🚀 部署说明

### 生产环境部署

1. **配置生产环境**
   ```bash
   # 设置生产环境配置
   export SPRING_PROFILES_ACTIVE=prod
   ```

2. **构建生产版本**
   ```bash
   mvn clean package -Pprod
   ```

3. **运行应用**
   ```bash
   java -jar target/shopping-cart-0.0.1-SNAPSHOT.jar
   ```

### Docker部署

```dockerfile
FROM openjdk:17-jdk-slim

COPY target/shopping-cart-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app.jar"]
```

```bash
# 构建镜像
docker build -t springca-shopping-cart .

# 运行容器
docker run -p 8080:8080 springca-shopping-cart
```

## 👤 测试账户

系统提供了以下测试账户：

| 用户名 | 密码 | 角色 | 描述 |
|--------|------|------|------|
| admin | admin123 | 管理员 | 系统管理员账户 |
| john | john123 | 用户 | 普通用户账户 |
| alice | alice123 | 用户 | 普通用户账户 |
| bob | bob123 | 用户 | 普通用户账户 |

## 📈 开发历程

### Sprint 1 - 基础功能 ✅
- **目标**: 用户管理和商品管理
- **完成**: 用户注册登录、商品浏览搜索、基础页面模板
- **技术**: Spring Boot + Thymeleaf + Bootstrap

### Sprint 2 - 购物车与订单 ✅
- **目标**: 完整的购物流程
- **完成**: 购物车管理、订单处理、库存管理、用户体验优化
- **技术**: JPA事务管理、AJAX交互、性能优化

### Sprint 3 - React集成 ✅
- **目标**: 现代化前端技术栈
- **完成**: React组件开发、REST API、前后端分离、全局异常处理
- **技术**: React + Webpack + REST API

### Sprint 4 - 系统完善 🚀
- **计划**: 功能扩展和系统优化
- **目标**: 更多React组件、性能优化、测试覆盖、部署优化

## 🤝 贡献指南

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

### 代码规范

- 遵循 Java 代码规范
- 使用有意义的变量和方法名
- 添加必要的注释和文档
- 确保测试通过

## 📄 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 📞 联系方式

- **项目团队**: SpringCA开发组
- **邮箱**: springca-team@example.com
- **项目地址**: [GitHub Repository](https://github.com/your-username/SpringCA)

## 🙏 致谢

感谢以下开源项目的支持：

- [Spring Boot](https://spring.io/projects/spring-boot)
- [React](https://reactjs.org/)
- [Bootstrap](https://getbootstrap.com/)
- [H2 Database](https://www.h2database.com/)
- [Maven](https://maven.apache.org/)

---

**最后更新**: 2024年9月26日  
**版本**: v3.0  
**状态**: 积极开发中 🚀