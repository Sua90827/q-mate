import {
  RegisterBody,
  RegisterResponse,
  ResendEmailVerificationBody,
  SendEmailVerificationBody,
  VerifyEmailCodeBody,
  VerifyEmailCodeResponse,
} from '@/types/signupType';
import instance from '../lib/axiosInstance';

// 1) 이메일 인증 코드 발송
export const sendEmailVerification = async (payload: SendEmailVerificationBody) => {
  const { data } = await instance.post('/auth/email-verifications', payload);
  return data as { sent: true };
};

// 2) 이메일 인증 코드 재전송
export const resendEmailVerification = async (payload: ResendEmailVerificationBody) => {
  const { data } = await instance.post('/auth/email-verifications/resend', payload);
  return data as { resent: true };
};

// 3) 이메일 인증 코드 검증 (토큰 획득)
export const verifyEmailCode = async (payload: VerifyEmailCodeBody) => {
  const { data } = await instance.post('/auth/email-verifications/verify', payload);
  return data as VerifyEmailCodeResponse;
};

// 4) 자체 회원가입 (헤더에 검증 토큰 포함)
export const registerWithEmailToken = async (body: RegisterBody) => {
  const { data } = await instance.post('/auth/register', body);
  return data as RegisterResponse; // { registered: true }
};
