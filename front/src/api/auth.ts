import { instance } from './axiosInstance';

//자체 로그인
export const loginUser = async ({ email, password }: { email: string; password: string }) => {
  const res = await instance.post(`/auth/login`, { email, password });
  return res.data;
};

//로그아웃
export const logoutUser = async () => {
  const res = await instance.post('/auth/logout', {}, { withCredentials: true });
  return res.data;
};

//소셜 로그인
export const socialLogin = (provider: string) => {
  const url = `http://ec2-43-201-32-144.ap-northeast-2.compute.amazonaws.com:8080/oauth2/authorization/${provider}`;
  window.location.href = url;
};

//추가 폼 정보 입력
export const updateSocialProfile = async ({
  nickname,
  birthDate,
}: {
  nickname: string;
  birthDate: string;
}) => {
  const res = await instance.patch(`api/users/me/profile`, { nickname, birthDate });
  return res.data;
};
