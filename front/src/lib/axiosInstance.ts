import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';
import { handleUnauthorized } from '@/lib/redirectHandler';

//일반api용
export const instance = axios.create({
  baseURL: '/api',
  withCredentials: false,
});

// 요청 인터셉터
instance.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().accessToken;
    const requestUrl = config.url ?? '';

    // 인증 제외 라우트
    const isPublicRoute =
      requestUrl.includes('/auth/login') ||
      requestUrl.includes('/auth/register') ||
      requestUrl.includes('/auth/email-verifications') ||
      requestUrl.includes('/auth/email-verifications/resend') ||
      requestUrl.includes('/auth/email-verifications/verify') ||
      requestUrl.includes('/auth/google/exchange');

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
      requestUrl.includes('/auth/email-verifications/resend') ||
      requestUrl.includes('/auth/email-verifications/verify') ||
      requestUrl.includes('/auth/google/exchange');

    if (status === 401 && !isAuthRoute) {
      handleUnauthorized();
    }

    return Promise.reject(error);
  },
);

export default instance;
