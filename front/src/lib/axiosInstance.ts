import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';
import { handleUnauthorized } from '@/lib/redirectHandler';

// 일반 API용 axios 인스턴스
export const instance = axios.create({
  baseURL: '/api',
  withCredentials: false,
});

// 요청 인터셉터
instance.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().accessToken;
    const requestUrl = config.url ?? '';

    // 인증 제외 라우트 (정규식 기반)
    const isPublicRoute = /\/auth\/(login|register|email-verifications|google\/exchange)/.test(
      requestUrl,
    );

    if (token && !isPublicRoute) {
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
    const requestUrl = error.config?.url ?? '';

    // 인증 관련 라우트 (정규식 기반)
    const isAuthRoute = /\/auth\/(login|register|email-verifications|google\/exchange)/.test(
      requestUrl,
    );

    if (status === 401 && !isAuthRoute) {
      handleUnauthorized();
    }

    return Promise.reject(error);
  },
);

export default instance;
