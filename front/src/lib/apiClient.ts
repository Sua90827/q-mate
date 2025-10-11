import axios from 'axios';

export const api = axios.create({
  baseURL: '',
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
