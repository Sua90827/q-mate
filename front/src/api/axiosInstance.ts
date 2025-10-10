import axios from 'axios';
import { useAuthStore } from '@/store/useAuthStore';
import { handleUnauthorized } from '@/lib/redirectHandler';

export const instance = axios.create({
  baseURL: '/api',
  withCredentials: false,
});

instance.interceptors.request.use(
  (config) => {
    console.log('accessToken:', useAuthStore.getState().accessToken);
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
    if (error.response?.status === 401) {
      handleUnauthorized();
    }
    return Promise.reject(error);
  },
);

export default instance;
