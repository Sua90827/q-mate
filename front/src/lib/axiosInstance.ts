import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';
import { useRouter } from 'next/navigation';
import { ErrorToast } from '@/components/common/CustomToast';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
});

// 요청 시 토큰 자동 첨부
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// 응답 시 401(만료) 감지
api.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      const router = useRouter();
      const { resetAccessToken } = useAuthStore.getState();
      ErrorToast('로그인이 만료되어 다시 로그인 화면으로 이동합니다.');
      resetAccessToken(); // Zustand 초기화
      setTimeout(() => router.push('/login'), 1500);
    }
    return Promise.reject(error);
  },
);

export default api;
