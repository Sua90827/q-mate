import { loginUser } from '@/api/login';
import { useMutation } from '@tanstack/react-query';

//로그인
export const useLoginUser = () => {
  return useMutation({
    mutationFn: ({ email, password }: { email: string; password: string }) =>
      loginUser({ email, password }),
  });
};
