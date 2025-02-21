import React from 'react';
import ReactDOM from 'react-dom';
import App from './App';
import './styles/global.css'; // 스타일 파일 추가 (선택 사항)

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);
