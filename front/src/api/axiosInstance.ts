import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';
import { handleUnauthorized } from '@/lib/redirectHandler';

export const instance = axios.create({
  baseURL: process.env.NEXT_PUBLIC_BACKEND_ORIGIN,
  withCredentials: false, // Bearer 전략이면 false 유지
});

// 요청 인터셉터
instance.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().accessToken;
    const requestUrl = config.url ?? '';

    // 인증 제외 라우트 (토큰 필요 없음)
    const isPublicRoute =
      requestUrl.includes('/auth/login') ||
      requestUrl.includes('/auth/register') ||
      requestUrl.includes('/auth/email-verifications') ||
      requestUrl.includes('/auth/google/exchange') ||
      requestUrl.includes('/auth/email-verifications/resend') ||
      requestUrl.includes('/auth/email-verifications/verify') ||
      requestUrl.includes('/api/users/me/profile');

    // 토큰이 있고, 공개 라우트가 아니라면 Authorization 헤더 추가
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

    const isAuthRoute =
      requestUrl.includes('/auth/login') ||
      requestUrl.includes('/auth/register') ||
      requestUrl.includes('/auth/email-verifications') ||
      requestUrl.includes('/auth/google/exchange') ||
      requestUrl.includes('/auth/email-verifications/resend') ||
      requestUrl.includes('/auth/email-verifications/verify');

    if (status === 401 && !isAuthRoute) {
      handleUnauthorized();
    }

    return Promise.reject(error);
  },
);

export default instance;
