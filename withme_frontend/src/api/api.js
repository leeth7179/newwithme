import axios from "axios";

/**
 * Axios 인스턴스를 생성하여 백엔드와 통신하는 API 설정
 * baseURL: Spring Boot 서버의 기본 URL
 * headers: 요청 헤더에 Content-Type 설정
 */
const api = axios.create({
  baseURL: "http://localhost:8080/api", // Spring Boot 백엔드 URL
  headers: {
    "Content-Type": "application/json", // JSON 데이터를 서버로 전송
  },
});

/**
 * 🔹 요청 인터셉터 추가: 모든 요청에 자동으로 인증 토큰 추가
 */
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token"); // 🔹 로컬 스토리지에서 토큰 가져오기
    if (token) {
      config.headers.Authorization = `Bearer ${token}`; // 🔹 Authorization 헤더 추가
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

export default api; // 다른 파일에서 api 객체를 사용할 수 있도록 내보내기
