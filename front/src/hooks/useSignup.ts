import {
  registerWithEmailToken,
  resendEmailVerification,
  sendEmailVerification,
  verifyEmailCode,
} from '@/api/signup';
import {
  RegisterBody,
  ResendEmailVerificationBody,
  SendEmailVerificationBody,
  VerifyEmailCodeBody,
} from '@/types/signupType';
import { useMutation } from '@tanstack/react-query';

// 이메일 인증코드 발송
export const useSendEmailVerification = () =>
  useMutation({
    mutationFn: (payload: SendEmailVerificationBody) => sendEmailVerification(payload),
  });

// 이메일 인증코드 재전송
export const useResendEmailVerification = () =>
  useMutation({
    mutationFn: (payload: ResendEmailVerificationBody) => resendEmailVerification(payload),
  });

// 이메일 인증코드 확인
export const useVerifyEmailCode = () =>
  useMutation({
    mutationFn: (payload: VerifyEmailCodeBody) => verifyEmailCode(payload),
  });

// 자체 회원가입
export const useRegisterWithEmailToken = () =>
  useMutation({
    mutationFn: (vars: { body: RegisterBody }) => registerWithEmailToken(vars.body),
  });
