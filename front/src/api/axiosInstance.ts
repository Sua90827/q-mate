import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';
import { handleUnauthorized } from '@/lib/redirectHandler';

// 공용 axios 인스턴스 생성
export const instance = axios.create({
  baseURL: '/api',
  withCredentials: false,
});

// 요청 인터셉터
instance.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().accessToken;
    const requestUrl = config.url ?? '';

    // 로그인 및 회원가입 요청에는 Authorization 헤더를 추가하지 않음
    const isAuthRoute = requestUrl.includes('/auth/login') || requestUrl.includes('/auth/register');

    if (token && !isAuthRoute) {
      config.headers.Authorization = `Bearer ${token}`;
    }

    return config;
  },
  (error) => Promise.reject(error),
);

// 응답 인터셉터
instance.interceptors.response.use(
  (res) => res,
  (error) => {
    const status = error.response?.status;
    const requestUrl = error.config?.url;

    // 로그인 및 회원가입 요청에서 발생한 401은 무시
    const isAuthRoute = requestUrl.includes('/auth/login') || requestUrl.includes('/auth/register');
    if (status === 401 && !isAuthRoute) {
      handleUnauthorized();
    }

    return Promise.reject(error);
  },
);

export default instance;
