-- SpringCA MySQL数据库设置脚本
-- 请在MySQL命令行或MySQL Workbench中执行以下命令

-- 1. 创建数据库
CREATE DATABASE IF NOT EXISTS shopping_cart 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

-- 2. 创建专用用户（推荐）
CREATE USER IF NOT EXISTS 'springca_user'@'localhost' IDENTIFIED BY 'springca_password123';

-- 3. 授予权限
GRANT ALL PRIVILEGES ON shopping_cart.* TO 'springca_user'@'localhost';

-- 4. 刷新权限
FLUSH PRIVILEGES;

-- 5. 验证数据库创建
SHOW DATABASES;

-- 6. 使用数据库
USE shopping_cart;

-- 7. 显示当前数据库
SELECT DATABASE();