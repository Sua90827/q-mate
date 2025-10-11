import { instance } from '../lib/axiosInstance';

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

//구글 소셜 로그인
export const googleLogin = (provider: string) => {
  const frontendUrl = process.env.NEXT_PUBLIC_FRONTEND_ORIGIN;
  const clientId = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;

  if (!frontendUrl || !clientId) {
    return;
  }

  // 리다이렉트 URI
  const redirectUri = `${frontendUrl}/login/oauth2/code/${provider}`;

  // 로그인 URL 생성
  const authUrl = new URL('https://accounts.google.com/o/oauth2/v2/auth');
  authUrl.searchParams.set('client_id', clientId);
  authUrl.searchParams.set('redirect_uri', redirectUri);
  authUrl.searchParams.set('response_type', 'code');
  authUrl.searchParams.set('scope', 'email profile openid');
  authUrl.searchParams.set('access_type', 'offline');

  // 페이지 이동 (전체 리다이렉트)
  window.location.href = authUrl.toString();
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
