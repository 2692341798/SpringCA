import React, { useState, useEffect, useCallback } from 'react';
import { productApi, cartApi } from '../services/api';
import { formatPrice, formatRating, generateStars, debounce, showToast, getImageUrl } from '../utils/helpers';

const ProductList = () => {
  // 状态管理
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [pagination, setPagination] = useState({
    currentPage: 0,
    totalPages: 0,
    totalElements: 0,
    size: 12,
    hasNext: false,
    hasPrevious: false
  });
  
  // 筛选和搜索状态
  const [filters, setFilters] = useState({
    query: '',
    category: '',
    brand: '',
    sortBy: 'name',
    sortDir: 'asc'
  });
  
  const [categories, setCategories] = useState([]);
  const [brands, setBrands] = useState([]);
  const [cartCount, setCartCount] = useState(0);

  // 加载商品数据
  const loadProducts = useCallback(async (page = 0) => {
    try {
      setLoading(true);
      setError(null);
      
      const params = {
        ...filters,
        page,
        size: pagination.size
      };
      
      const response = await productApi.getProducts(params);
      
      if (response.success) {
        setProducts(response.data);
        setPagination(response.pagination);
      } else {
        throw new Error(response.message || '加载商品失败');
      }
    } catch (err) {
      setError(err.message);
      showToast(err.message, 'error');
    } finally {
      setLoading(false);
    }
  }, [filters, pagination.size]);

  // 加载分类和品牌
  const loadFilters = useCallback(async () => {
    try {
      const [categoriesRes, brandsRes] = await Promise.all([
        productApi.getCategories(),
        productApi.getBrands()
      ]);
      
      if (categoriesRes.success) {
        setCategories(categoriesRes.data);
      }
      
      if (brandsRes.success) {
        setBrands(brandsRes.data);
      }
    } catch (err) {
      console.error('加载筛选选项失败:', err);
    }
  }, []);

  // 加载购物车数量
  const loadCartCount = useCallback(async () => {
    try {
      const response = await cartApi.getCartCount();
      if (response.success) {
        setCartCount(response.data.count);
      }
    } catch (err) {
      console.error('加载购物车数量失败:', err);
    }
  }, []);

  // 防抖搜索
  const debouncedSearch = useCallback(
    debounce((searchQuery) => {
      setFilters(prev => ({ ...prev, query: searchQuery }));
    }, 500),
    []
  );

  // 添加到购物车
  const handleAddToCart = async (productId, productName) => {
    try {
      const response = await cartApi.addToCart(productId, 1);
      
      if (response.success) {
        showToast(`${productName} 已添加到购物车`, 'success');
        setCartCount(response.data.totalQuantity);
      } else {
        throw new Error(response.message || '添加失败');
      }
    } catch (err) {
      showToast(err.message, 'error');
    }
  };

  // 处理筛选变化
  const handleFilterChange = (key, value) => {
    setFilters(prev => ({ ...prev, [key]: value }));
  };

  // 处理分页
  const handlePageChange = (newPage) => {
    if (newPage >= 0 && newPage < pagination.totalPages) {
      loadProducts(newPage);
    }
  };

  // 初始化加载
  useEffect(() => {
    loadFilters();
    loadCartCount();
  }, [loadFilters, loadCartCount]);

  // 当筛选条件变化时重新加载
  useEffect(() => {
    loadProducts(0);
  }, [loadProducts]);

  // 渲染商品卡片
  const renderProductCard = (product) => (
    <div key={product.id} className="col-md-6 col-lg-4 col-xl-3 mb-4">
      <div className="card h-100 product-card">
        <div className="position-relative">
          <img 
            src={getImageUrl(product.imageUrl)} 
            className="card-img-top product-image" 
            alt={product.name}
            style={{ height: '200px', objectFit: 'cover' }}
          />
          {product.stock <= 5 && product.stock > 0 && (
            <span className="badge bg-warning position-absolute top-0 end-0 m-2">
              仅剩{product.stock}件
            </span>
          )}
          {product.stock === 0 && (
            <span className="badge bg-danger position-absolute top-0 end-0 m-2">
              缺货
            </span>
          )}
        </div>
        
        <div className="card-body d-flex flex-column">
          <h6 className="card-title text-truncate" title={product.name}>
            {product.name}
          </h6>
          
          <p className="card-text text-muted small flex-grow-1">
            {product.description && product.description.length > 60 
              ? product.description.substring(0, 60) + '...' 
              : product.description}
          </p>
          
          <div className="mb-2">
            <div className="d-flex align-items-center mb-1">
              <span 
                className="me-2" 
                dangerouslySetInnerHTML={{ __html: generateStars(product.rating || 0) }}
              />
              <small className="text-muted">
                {formatRating(product.rating || 0)} ({product.reviewCount || 0})
              </small>
            </div>
            
            <div className="d-flex justify-content-between align-items-center">
              <span className="h6 text-primary mb-0">
                {formatPrice(product.price)}
              </span>
              <small className="text-muted">{product.category}</small>
            </div>
          </div>
          
          <div className="mt-auto">
            <button 
              className="btn btn-primary btn-sm w-100"
              onClick={() => handleAddToCart(product.id, product.name)}
              disabled={product.stock === 0}
            >
              {product.stock === 0 ? '缺货' : '加入购物车'}
            </button>
          </div>
        </div>
      </div>
    </div>
  );

  // 渲染分页
  const renderPagination = () => {
    if (pagination.totalPages <= 1) return null;

    const pages = [];
    const maxVisiblePages = 5;
    const startPage = Math.max(0, pagination.currentPage - Math.floor(maxVisiblePages / 2));
    const endPage = Math.min(pagination.totalPages - 1, startPage + maxVisiblePages - 1);

    return (
      <nav aria-label="商品分页">
        <ul className="pagination justify-content-center">
          <li className={`page-item ${!pagination.hasPrevious ? 'disabled' : ''}`}>
            <button 
              className="page-link" 
              onClick={() => handlePageChange(pagination.currentPage - 1)}
              disabled={!pagination.hasPrevious}
            >
              上一页
            </button>
          </li>
          
          {startPage > 0 && (
            <>
              <li className="page-item">
                <button className="page-link" onClick={() => handlePageChange(0)}>1</button>
              </li>
              {startPage > 1 && <li className="page-item disabled"><span className="page-link">...</span></li>}
            </>
          )}
          
          {Array.from({ length: endPage - startPage + 1 }, (_, i) => startPage + i).map(page => (
            <li key={page} className={`page-item ${page === pagination.currentPage ? 'active' : ''}`}>
              <button 
                className="page-link" 
                onClick={() => handlePageChange(page)}
              >
                {page + 1}
              </button>
            </li>
          ))}
          
          {endPage < pagination.totalPages - 1 && (
            <>
              {endPage < pagination.totalPages - 2 && <li className="page-item disabled"><span className="page-link">...</span></li>}
              <li className="page-item">
                <button className="page-link" onClick={() => handlePageChange(pagination.totalPages - 1)}>
                  {pagination.totalPages}
                </button>
              </li>
            </>
          )}
          
          <li className={`page-item ${!pagination.hasNext ? 'disabled' : ''}`}>
            <button 
              className="page-link" 
              onClick={() => handlePageChange(pagination.currentPage + 1)}
              disabled={!pagination.hasNext}
            >
              下一页
            </button>
          </li>
        </ul>
      </nav>
    );
  };

  return (
    <div className="container-fluid py-4">
      {/* 头部导航 */}
      <div className="row mb-4">
        <div className="col-12">
          <nav className="navbar navbar-expand-lg navbar-light bg-light rounded">
            <div className="container-fluid">
              <span className="navbar-brand mb-0 h1">
                <i className="bi bi-shop me-2"></i>
                SpringCA 商品列表
              </span>
              
              <div className="d-flex align-items-center">
                <span className="badge bg-primary me-3">
                  <i className="bi bi-cart me-1"></i>
                  购物车 ({cartCount})
                </span>
              </div>
            </div>
          </nav>
        </div>
      </div>

      {/* 搜索和筛选 */}
      <div className="row mb-4">
        <div className="col-12">
          <div className="card">
            <div className="card-body">
              <div className="row g-3">
                {/* 搜索框 */}
                <div className="col-md-4">
                  <div className="input-group">
                    <span className="input-group-text">
                      <i className="bi bi-search"></i>
                    </span>
                    <input
                      type="text"
                      className="form-control"
                      placeholder="搜索商品..."
                      onChange={(e) => debouncedSearch(e.target.value)}
                    />
                  </div>
                </div>
                
                {/* 分类筛选 */}
                <div className="col-md-2">
                  <select
                    className="form-select"
                    value={filters.category}
                    onChange={(e) => handleFilterChange('category', e.target.value)}
                  >
                    <option value="">所有分类</option>
                    {categories.map(category => (
                      <option key={category} value={category}>{category}</option>
                    ))}
                  </select>
                </div>
                
                {/* 品牌筛选 */}
                <div className="col-md-2">
                  <select
                    className="form-select"
                    value={filters.brand}
                    onChange={(e) => handleFilterChange('brand', e.target.value)}
                  >
                    <option value="">所有品牌</option>
                    {brands.map(brand => (
                      <option key={brand} value={brand}>{brand}</option>
                    ))}
                  </select>
                </div>
                
                {/* 排序 */}
                <div className="col-md-2">
                  <select
                    className="form-select"
                    value={`${filters.sortBy}-${filters.sortDir}`}
                    onChange={(e) => {
                      const [sortBy, sortDir] = e.target.value.split('-');
                      handleFilterChange('sortBy', sortBy);
                      handleFilterChange('sortDir', sortDir);
                    }}
                  >
                    <option value="name-asc">名称 A-Z</option>
                    <option value="name-desc">名称 Z-A</option>
                    <option value="price-asc">价格低到高</option>
                    <option value="price-desc">价格高到低</option>
                    <option value="rating-desc">评分高到低</option>
                  </select>
                </div>
                
                {/* 每页数量 */}
                <div className="col-md-2">
                  <select
                    className="form-select"
                    value={pagination.size}
                    onChange={(e) => setPagination(prev => ({ ...prev, size: parseInt(e.target.value) }))}
                  >
                    <option value={12}>12个/页</option>
                    <option value={24}>24个/页</option>
                    <option value={48}>48个/页</option>
                  </select>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 商品列表 */}
      <div className="row mb-4">
        <div className="col-12">
          {loading && (
            <div className="text-center py-5">
              <div className="spinner-border text-primary" role="status">
                <span className="visually-hidden">加载中...</span>
              </div>
              <p className="mt-2">正在加载商品...</p>
            </div>
          )}

          {error && (
            <div className="alert alert-danger" role="alert">
              <i className="bi bi-exclamation-triangle me-2"></i>
              {error}
            </div>
          )}

          {!loading && !error && products.length === 0 && (
            <div className="text-center py-5">
              <i className="bi bi-inbox display-1 text-muted"></i>
              <p className="mt-3 text-muted">没有找到符合条件的商品</p>
            </div>
          )}

          {!loading && !error && products.length > 0 && (
            <>
              <div className="d-flex justify-content-between align-items-center mb-3">
                <span className="text-muted">
                  共找到 {pagination.totalElements} 个商品，
                  第 {pagination.currentPage + 1} 页，共 {pagination.totalPages} 页
                </span>
              </div>
              
              <div className="row">
                {products.map(renderProductCard)}
              </div>
            </>
          )}
        </div>
      </div>

      {/* 分页 */}
      {!loading && !error && products.length > 0 && (
        <div className="row">
          <div className="col-12">
            {renderPagination()}
          </div>
        </div>
      )}
    </div>
  );
};

export default ProductList;