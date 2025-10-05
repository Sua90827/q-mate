import axios from 'axios';

//자체 로그인
export const loginUser = async ({ email, password }: { email: string; password: string }) => {
  const res = await axios.post(`/auth/login`, { email, password });
  return res.data;
};

//소셜 로그인
export const socialLogin = async (provider: string) => {
  const res = await axios.post(`/oauth2/authorization/${provider}`);
  return res.data;
};
