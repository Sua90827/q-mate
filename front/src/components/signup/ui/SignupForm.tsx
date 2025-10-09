'use client';

import { UseFormRegister, FieldErrors, Control } from 'react-hook-form';
import { SignupFormValues } from '@/utils/validation/signupValidation';
import { FormField, FormItem, FormControl, FormMessage } from '@/components/ui/form';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import { Loader2 } from 'lucide-react';
import SignupDatePicker from './SignupDatePicker';

type Props = {
  register: UseFormRegister<SignupFormValues>;
  errors: FieldErrors<SignupFormValues>;
  onSubmit: (e?: React.BaseSyntheticEvent) => void;
  onClickSendCode: () => void;
  onClickResendCode: () => void;
  onClickVerifyCode: () => void;
  control: Control<SignupFormValues>;
  isSendingCode?: boolean;
  isVerifying?: boolean;
  isSubmitting?: boolean;
  isCodeSent: boolean;
};

export default function SignupForm({
  register,
  errors,
  onSubmit,
  onClickSendCode,
  onClickResendCode,
  onClickVerifyCode,
  control,
  isSendingCode,
  isVerifying,
  isSubmitting,
  isCodeSent,
}: Props) {
  return (
    <form onSubmit={onSubmit} className="flex flex-col gap-3">
      {/* 이메일 + 코드발송 */}
      <div className="grid grid-cols-[1fr_auto] gap-2">
        <FormField
          name="email"
          render={() => (
            <FormItem>
              <FormControl>
                <Input
                  placeholder="이메일"
                  className="h-11 bg-secondary rounded-md  !text-14"
                  {...register('email')}
                />
              </FormControl>
              <FormMessage className="text-xs">{errors.email?.message}</FormMessage>
            </FormItem>
          )}
        />
        <Button
          type="button"
          onClick={isCodeSent ? onClickResendCode : onClickSendCode}
          disabled={isSendingCode}
          className="h-11 w-20 !font-semibold !text-14 text-secondary "
        >
          {isSendingCode ? (
            <Loader2 className="w-4 h-4 animate-spin" />
          ) : isCodeSent ? (
            '재전송'
          ) : (
            '코드발송'
          )}
        </Button>
      </div>

      {/* 코드 + 인증확인 */}
      <div className="grid grid-cols-[1fr_auto] gap-2">
        <FormField
          name="code"
          render={() => (
            <FormItem>
              <FormControl>
                <Input
                  placeholder="인증번호 입력"
                  className="h-11 bg-secondary rounded-md text-text-secondary !text-14"
                  {...register('code')}
                />
              </FormControl>
              <FormMessage className="text-xs">{errors.code?.message}</FormMessage>
            </FormItem>
          )}
        />
        <Button
          type="button"
          onClick={onClickVerifyCode}
          disabled={isVerifying}
          className="h-11 w-20 !font-semibold !text-14 text-secondary "
        >
          {isVerifying ? <Loader2 className="w-4 h-4 animate-spin  text-secondary " /> : '인증확인'}
        </Button>
      </div>

      {/* 비밀번호 */}
      <FormField
        name="password"
        render={() => (
          <FormItem>
            <FormControl>
              <Input
                type="password"
                placeholder="비밀번호"
                className="h-11 bg-secondary rounded-md text-text-secondary !text-14"
                {...register('password')}
              />
            </FormControl>

            <FormMessage className="text-xs">{errors.password?.message}</FormMessage>
          </FormItem>
        )}
      />

      {/* 닉네임 */}
      <FormField
        name="nickname"
        render={() => (
          <FormItem>
            <FormControl>
              <Input
                placeholder="닉네임"
                className="h-11 bg-secondary rounded-md text-text-secondary !text-14"
                {...register('nickname')}
              />
            </FormControl>
            <FormMessage className="text-xs">{errors.nickname?.message}</FormMessage>
          </FormItem>
        )}
      />

      {/* 생년월일 */}
      <FormField
        name="birth"
        render={() => (
          <FormItem>
            <FormControl>
              <SignupDatePicker control={control} />
            </FormControl>
            <FormMessage className="text-xs">{errors.birth?.message}</FormMessage>
          </FormItem>
        )}
      />

      {/* 회원가입 */}
      <Button
        type="submit"
        className="w-full h-[42px] font-semibold !text-16 text-secondary"
        disabled={isSubmitting}
      >
        {isSubmitting ? '회원가입 중…' : '회원가입'}
      </Button>
    </form>
  );
}
