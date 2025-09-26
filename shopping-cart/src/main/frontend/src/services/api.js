import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    // 可以在这里添加认证token等
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    // 统一错误处理
    const errorMessage = error.response?.data?.message || error.message || '请求失败';
    console.error('API Error:', errorMessage);
    return Promise.reject(new Error(errorMessage));
  }
);

// 商品相关API
export const productApi = {
  // 获取商品列表
  getProducts: (params = {}) => {
    return api.get('/products', { params });
  },
  
  // 获取商品详情
  getProduct: (id) => {
    return api.get(`/products/${id}`);
  },
  
  // 获取商品分类
  getCategories: () => {
    return api.get('/products/categories');
  },
  
  // 获取商品品牌
  getBrands: () => {
    return api.get('/products/brands');
  },
  
  // 获取搜索建议
  getSearchSuggestions: (query) => {
    return api.get('/products/suggestions', { params: { q: query } });
  },
};

// 购物车相关API
export const cartApi = {
  // 获取购物车
  getCart: () => {
    return api.get('/cart');
  },
  
  // 添加商品到购物车
  addToCart: (productId, quantity = 1) => {
    return api.post('/cart/add', null, {
      params: { productId, quantity }
    });
  },
  
  // 更新购物车商品数量
  updateCartItem: (cartItemId, quantity) => {
    return api.put('/cart/update', null, {
      params: { cartItemId, quantity }
    });
  },
  
  // 删除购物车商品
  removeFromCart: (cartItemId) => {
    return api.delete(`/cart/remove/${cartItemId}`);
  },
  
  // 清空购物车
  clearCart: () => {
    return api.delete('/cart/clear');
  },
  
  // 获取购物车数量
  getCartCount: () => {
    return api.get('/cart/count');
  },
};

export default api;