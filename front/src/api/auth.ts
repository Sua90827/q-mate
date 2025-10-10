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
  const frontendUrl = process.env.NEXT_PUBLIC_FRONTEND_ORIGIN;
  const clientId = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;
  const redirectUri = `${frontendUrl}/login/oauth2/code/${provider}`;

  const googleUrl =
    `https://accounts.google.com/o/oauth2/v2/auth` +
    `?client_id=${clientId}` +
    `&redirect_uri=${encodeURIComponent(redirectUri)}` +
    `&response_type=code` +
    `&scope=email profile openid` +
    `&access_type=offline`;

  window.location.href = googleUrl;
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
