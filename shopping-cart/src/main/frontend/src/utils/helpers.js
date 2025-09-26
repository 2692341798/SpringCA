// 格式化价格
export const formatPrice = (price) => {
  if (typeof price !== 'number') {
    price = parseFloat(price) || 0;
  }
  return `¥${price.toFixed(2)}`;
};

// 格式化评分
export const formatRating = (rating) => {
  if (typeof rating !== 'number') {
    rating = parseFloat(rating) || 0;
  }
  return rating.toFixed(1);
};

// 生成星级评分HTML
export const generateStars = (rating, maxStars = 5) => {
  const fullStars = Math.floor(rating);
  const hasHalfStar = rating % 1 >= 0.5;
  const emptyStars = maxStars - fullStars - (hasHalfStar ? 1 : 0);
  
  let stars = '';
  
  // 满星
  for (let i = 0; i < fullStars; i++) {
    stars += '<i class="bi bi-star-fill text-warning"></i>';
  }
  
  // 半星
  if (hasHalfStar) {
    stars += '<i class="bi bi-star-half text-warning"></i>';
  }
  
  // 空星
  for (let i = 0; i < emptyStars; i++) {
    stars += '<i class="bi bi-star text-muted"></i>';
  }
  
  return stars;
};

// 防抖函数
export const debounce = (func, wait) => {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
};

// 节流函数
export const throttle = (func, limit) => {
  let inThrottle;
  return function executedFunction(...args) {
    if (!inThrottle) {
      func.apply(this, args);
      inThrottle = true;
      setTimeout(() => inThrottle = false, limit);
    }
  };
};

// 检查是否为空对象
export const isEmpty = (obj) => {
  return Object.keys(obj).length === 0;
};

// 深拷贝
export const deepClone = (obj) => {
  return JSON.parse(JSON.stringify(obj));
};

// 生成唯一ID
export const generateId = () => {
  return Date.now().toString(36) + Math.random().toString(36).substr(2);
};

// 显示Toast消息
export const showToast = (message, type = 'info') => {
  // 创建toast元素
  const toastId = generateId();
  const toastHtml = `
    <div id="${toastId}" class="toast align-items-center text-white bg-${type === 'error' ? 'danger' : type === 'success' ? 'success' : 'primary'} border-0" role="alert" aria-live="assertive" aria-atomic="true">
      <div class="d-flex">
        <div class="toast-body">
          ${message}
        </div>
        <button type="button" class="btn-close btn-close-white me-2 m-auto" data-bs-dismiss="toast" aria-label="Close"></button>
      </div>
    </div>
  `;
  
  // 添加到页面
  let toastContainer = document.getElementById('toast-container');
  if (!toastContainer) {
    toastContainer = document.createElement('div');
    toastContainer.id = 'toast-container';
    toastContainer.className = 'toast-container position-fixed top-0 end-0 p-3';
    toastContainer.style.zIndex = '9999';
    document.body.appendChild(toastContainer);
  }
  
  toastContainer.insertAdjacentHTML('beforeend', toastHtml);
  
  // 显示toast
  const toastElement = document.getElementById(toastId);
  const toast = new window.bootstrap.Toast(toastElement, {
    autohide: true,
    delay: 3000
  });
  
  toast.show();
  
  // 自动清理
  toastElement.addEventListener('hidden.bs.toast', () => {
    toastElement.remove();
  });
};

// 确认对话框
export const showConfirm = (message, title = '确认') => {
  return new Promise((resolve) => {
    const confirmed = window.confirm(`${title}\n\n${message}`);
    resolve(confirmed);
  });
};

// 格式化文件大小
export const formatFileSize = (bytes) => {
  if (bytes === 0) return '0 Bytes';
  
  const k = 1024;
  const sizes = ['Bytes', 'KB', 'MB', 'GB'];
  const i = Math.floor(Math.log(bytes) / Math.log(k));
  
  return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
};

// 获取图片URL（处理相对路径）
export const getImageUrl = (imageUrl) => {
  if (!imageUrl) return '/images/placeholder.jpg';
  if (imageUrl.startsWith('http')) return imageUrl;
  return imageUrl.startsWith('/') ? imageUrl : `/${imageUrl}`;
};