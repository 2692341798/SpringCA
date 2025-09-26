#!/bin/bash

# SpringCA MySQL连接测试脚本

echo "🔍 测试MySQL连接..."

# 检查MySQL是否运行
if ! command -v mysql &> /dev/null; then
    echo "❌ MySQL未安装或不在PATH中"
    echo "请先安装MySQL: brew install mysql"
    exit 1
fi

# 测试MySQL服务是否运行
if ! mysqladmin ping -h localhost --silent; then
    echo "❌ MySQL服务未运行"
    echo "请启动MySQL服务: brew services start mysql"
    exit 1
fi

echo "✅ MySQL服务正在运行"

# 测试数据库连接
echo "🔗 测试数据库连接..."
mysql -h localhost -u springca_user -pspringca_password123 -e "SELECT 'MySQL连接成功!' as message;" shopping_cart 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ 数据库连接成功!"
    echo "📊 数据库信息:"
    mysql -h localhost -u springca_user -pspringca_password123 -e "
        SELECT 
            DATABASE() as current_database,
            USER() as current_user,
            VERSION() as mysql_version;
        SHOW TABLES;
    " shopping_cart 2>/dev/null
else
    echo "❌ 数据库连接失败"
    echo "请检查:"
    echo "1. MySQL服务是否运行: brew services start mysql"
    echo "2. 数据库是否存在: mysql -u root -p < mysql-setup.sql"
    echo "3. 用户权限是否正确"
fi