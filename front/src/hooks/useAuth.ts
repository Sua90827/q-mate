import { loginUser, logoutUser } from '@/api/auth';
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
