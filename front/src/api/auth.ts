import axios from 'axios';

//자체 로그인
export const loginUser = async ({ email, password }: { email: string; password: string }) => {
  const res = await axios.post(`/auth/login`, { email, password });
  return res.data;
};

//로그아웃
export const logoutUser = async () => {
  const res = await axios.post('/auth/logout');
  return res.data;
};

//소셜 로그인
export const socialLogin = async (provider: string) => {
  const res = await axios.post(`/oauth2/authorization/${provider}`);
  return res.data;
};

//추가 폼 정보 입력
export const updateSocialProfile = async ({
  nickname,
  birthDate,
}: {
  nickname: string;
  birthDate: string;
}) => {
  const res = await axios.patch(`/api/users/me/profile`, { nickname, birthDate });
  return res.data;
};
