import { useAuthStore } from '@/store/useAuthStore';
import axios from 'axios';

export const api = axios.create({
  baseURL: '',
  withCredentials: true,
});
api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().accessToken;
  if (token) {
    config.headers = config.headers ?? {};
    config.headers['Authorization'] = `Bearer ${token}`;
  }
  return config;
});

export default api;
