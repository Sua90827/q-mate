'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { signupValidation, SignupFormValues } from '@/utils/validation/signupValidation';
import SignupForm from './ui/SignupForm';
import { Form } from '@/components/ui/form';
import { SuccessToast, ErrorToast } from '@/components/common/CustomToast';
import {
  useRegisterWithEmailToken,
  useResendEmailVerification,
  useSendEmailVerification,
  useVerifyEmailCode,
} from '@/hooks/useSignup';
import Image from 'next/image';
import { useRouter } from 'next/navigation';

export default function SignupFormController() {
  const router = useRouter();

  const form = useForm<SignupFormValues>({
    resolver: zodResolver(signupValidation),
    mode: 'onChange',
    defaultValues: { email: '', code: '', password: '', nickname: '', birth: '' },
  });

  const sendCode = useSendEmailVerification();
  const resendCode = useResendEmailVerification();
  const verify = useVerifyEmailCode();
  const registerMut = useRegisterWithEmailToken();

  const [emailVerifiedToken, setEmailVerifiedToken] = useState<string | null>(null);
  const [isCodeSent, setIsCodeSent] = useState(false);
  const handleSendCode = async () => {
    const email = form.getValues('email');
    if (!email) {
      form.setError('email', { message: '이메일을 먼저 입력해주세요.' });
      return;
    }
    try {
      const res = await sendCode.mutateAsync({ email, purpose: 'SIGNUP' });
      if (res && res.sent === true) {
        setIsCodeSent(true);
        SuccessToast('인증 코드를 전송했습니다.', 'text-primary border-primary');
      } else {
        setIsCodeSent(false);
        ErrorToast('코드 전송에 실패했습니다.');
      }
    } catch {
      setIsCodeSent(false);
      ErrorToast('코드 전송에 실패했습니다.');
    }
  };
  const handleResendCode = async () => {
    const email = form.getValues('email');
    if (!email) {
      form.setError('email', { message: '이메일을 먼저 입력해주세요.' });
      return;
    }

    try {
      const res = await resendCode.mutateAsync({ email, purpose: 'SIGNUP' });
      if (res && res.resent === true) {
        SuccessToast('인증 코드를 재전송했습니다.', 'text-primary border-primary bg-bg-auth');
      } else {
        ErrorToast('코드 재전송에 실패했습니다.');
      }
    } catch {
      ErrorToast('코드 재전송에 실패했습니다.');
    }
  };
  const handleVerifyCode = async () => {
    const { email, code } = form.getValues();
    if (!email) form.setError('email', { message: '이메일을 먼저 입력해주세요.' });
    if (!code) form.setError('code', { message: '인증코드를 입력해주세요.' });
    if (!email || !code) return;

    try {
      const res = await verify.mutateAsync({ email, code, purpose: 'SIGNUP' });
      if (res?.verified) {
        if (res.email_verified_token) setEmailVerifiedToken(res.email_verified_token);
        if (res?.verified === true) {
          SuccessToast('인증에 성공했습니다.', 'text-primary border-primary bg-bg-auth');
        }
      } else {
        ErrorToast('인증에 실패했습니다.');
      }
    } catch {
      ErrorToast('인증 확인에 실패했습니다.');
    }
  };

  const onSubmit = async (values: SignupFormValues) => {
    try {
      if (!emailVerifiedToken) {
        ErrorToast('이메일 인증을 먼저 완료해주세요.');
        return;
      }
      const { email, password, nickname, birth } = values;
      const res = await registerMut.mutateAsync({
        body: { email, password, nickname, birthDate: birth, emailVerifiedToken },
      });
      if (res && res.registered === true) {
        SuccessToast('회원가입이 완료되었습니다.', 'text-primary border-primary bg-bg-auth');
        console.log(res.registered);
      } else {
        ErrorToast('회원가입에 실패했습니다.');
      }

      router.push('/login');
    } catch {
      ErrorToast('회원가입에 실패했습니다. 잠시 후 다시 시도해주세요.');
    }
  };

  return (
    <div className="w-full h-full flex flex-col gap-5 items-center justify-center pt-[70px] sm:pt-[0px] sm:pb-[70px]">
      <Image src="/images/logo/day_logo.svg" alt="큐메이트" width={173} height={55} priority />
      <Form {...form}>
        <SignupForm
          register={form.register}
          errors={form.formState.errors}
          onSubmit={form.handleSubmit(onSubmit)}
          onClickSendCode={handleSendCode}
          onClickResendCode={handleResendCode}
          onClickVerifyCode={handleVerifyCode}
          isSendingCode={sendCode.isPending}
          isVerifying={verify.isPending}
          isSubmitting={registerMut.isPending}
          control={form.control}
          isCodeSent={isCodeSent}
        />
      </Form>
    </div>
  );
}
