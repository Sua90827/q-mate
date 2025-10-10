import { loginUser, logoutUser, socialLogin, updateSocialProfile } from '@/api/auth';
import { useMutation } from '@tanstack/react-query';

//로그인
export const useLoginUser = () => {
  return useMutation({
    mutationFn: ({ email, password }: { email: string; password: string }) =>
      loginUser({ email, password }),
  });
};

//로그아웃
export const useLogoutUser = () => {
  return useMutation({
    mutationFn: () => logoutUser(),
  });
};

//소셜 로그인 추가 정보 폼
export const useSocialProfile = () => {
  return useMutation({
    mutationFn: ({ nickname, birthDate }: { nickname: string; birthDate: string }) =>
      updateSocialProfile({ nickname, birthDate }),
  });
};
