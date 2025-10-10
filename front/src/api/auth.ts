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
  const isMobile = window.innerWidth <= 768;
  const url = `/oauth2/authorization/${provider}`;

  if (isMobile) {
    // 전체 페이지 이동 (모바일)
    window.location.href = url;
  } else {
    // 팝업창 (데스크탑)
    const w = Math.min(500, window.innerWidth - 20);
    const h = Math.min(650, window.innerHeight - 40);
    const left = (window.innerWidth - w) / 2;
    const top = (window.innerHeight - h) / 2;

    window.open(url, 'oauth2', `width=${w},height=${h},left=${left},top=${top}`);
  }
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
