import axios from 'axios';

export const socialAxios = axios.create({
  baseURL: process.env.NEXT_PUBLIC_BACKEND_ORIGIN,
  withCredentials: true,
});

export default socialAxios;
