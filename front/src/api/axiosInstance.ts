import axios from 'axios';

// const instance = axios.create({
//   baseURL: process.env.NEXT_PUBLIC_API_URL,
// });
export const instance = axios.create({
  baseURL: '/api',
  withCredentials: false,
});

export default instance;
