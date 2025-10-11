import axios from 'axios';

export const api = axios.create({
  baseURL: '/api', // 프록시 경로
  withCredentials: true,
});

api.interceptors.request.use((config) => {
  const access = localStorage.getItem('accessToken');
  if (access) {
    config.headers = config.headers ?? {};
    config.headers['Authorization'] = `Bearer ${access}`;
  }
  return config;
});

export default api;
