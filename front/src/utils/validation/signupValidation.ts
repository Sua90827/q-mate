import { z } from 'zod';

export const signupValidation = z.object({
  email: z.string().email('올바른 이메일 형식을 입력해주세요.'),
  code: z.string().min(4, '인증코드를 입력해주세요.'),
  password: z
    .string()
    .min(8, '비밀번호는 최소 8자 이상이어야 합니다.')
    .regex(/[A-Za-z]/, '영문자를 포함해야 합니다.')
    .regex(/\d/, '숫자를 포함해야 합니다.')
    .regex(/[^A-Za-z0-9]/, '특수문자를 포함해야 합니다.'),
  nickname: z.string().min(2, '닉네임은 최소 2자 이상').max(10, '닉네임은 최대 10자 이하'),
  birth: z.string().min(1, '생년월일을 선택해주세요.'),
});

export type SignupFormValues = z.infer<typeof signupValidation>;
