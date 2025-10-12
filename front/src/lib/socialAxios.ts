import axios from 'axios';

export const socialAxios = axios.create({
  baseURL: '/auth',
  withCredentials: false,
});
