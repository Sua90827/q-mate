import { ErrorToast } from '@/components/common/CustomToast';
import { instance } from './axiosInstance';
import { AxiosError } from 'axios';

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

export const socialLogin = async (provider: string) => {
  try {
    const isMobile = window.innerWidth <= 768;
    const url = `/oauth2/authorization/${provider}`;

    const res = await instance.get(url, { withCredentials: true });

    if (res.data?.redirectUrl) {
      if (isMobile) {
        window.location.href = res.data.redirectUrl;
      } else {
        const w = Math.min(500, window.innerWidth - 20);
        const h = Math.min(650, window.innerHeight - 40);
        const left = (window.innerWidth - w) / 2;
        const top = (window.innerHeight - h) / 2;

        window.open(
          res.data.redirectUrl,
          'oauth2',
          `width=${w},height=${h},left=${left},top=${top}`,
        );
      }
    } else {
      ErrorToast('로그인 경로를 불러오지 못했습니다. 잠시 후 다시 시도해주세요.');
    }
  } catch (err) {
    const error = err as AxiosError;

    if (error.response?.status === 502) {
      ErrorToast('서버가 응답하지 않습니다. 잠시 후 다시 시도해주세요.');
    } else if (error.response?.status === 401) {
      ErrorToast('인증이 만료되었습니다. 다시 로그인해주세요.');
    } else {
      ErrorToast('소셜 로그인 중 오류가 발생했습니다.');
    }
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
