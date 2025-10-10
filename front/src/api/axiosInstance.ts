import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';
import { handleUnauthorized } from '@/lib/redirectHandler';

export const instance = axios.create({
  baseURL: '/api',
  withCredentials: false,
});

instance.interceptors.request.use(
  (config) => {
    const token = useAuthStore.getState().accessToken;
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error),
);

instance.interceptors.response.use(
  (res) => res,
  (error) => {
    const status = error.response?.status;
    const requestUrl = error.config?.url;

    const isAuthRoute = requestUrl?.startsWith('/login') || requestUrl?.startsWith('/signup');

    if (status === 401 && !isAuthRoute) {
      handleUnauthorized();
    }

    return Promise.reject(error);
  },
);

export default instance;
