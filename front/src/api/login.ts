import axios from 'axios';

//자체 로그인
export const loginUser = async ({ email, password }: { email: string; password: string }) => {
  const res = await axios.post(`/auth/login`, { email, password });
  return res.data;
};
