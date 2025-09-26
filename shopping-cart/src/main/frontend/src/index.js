import React from 'react';
import ReactDOM from 'react-dom/client';
import ProductList from './components/ProductList';
import './styles/main.css';

// 创建React应用根节点
const root = ReactDOM.createRoot(document.getElementById('root'));

// 渲染应用
root.render(
  <React.StrictMode>
    <ProductList />
  </React.StrictMode>
);