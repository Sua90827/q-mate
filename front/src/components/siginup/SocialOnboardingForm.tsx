'use client';
import React from 'react';
import { useForm } from 'react-hook-form';
import { Button } from '../common/Button';
import Image from 'next/image';
import { DateSelectButton } from '../common/DateSelectButton';
import { useRouter } from 'next/navigation';
import { useSocialProfile } from '@/hooks/useAuth';
import Loader from '../common/Loader';
import NoticeModal from '../common/NoticeModal';

interface FormValues {
  nickname: string;
  birthDate?: string;
}

export default function SocialOnboardingForm() {
  const router = useRouter();
  const { mutate: socialProfileMutate, isPending: updating } = useSocialProfile();
  const [open, setOpen] = React.useState(false);

  const storedNickname = sessionStorage.getItem('nickname') || '';
  const storedBirthDate = sessionStorage.getItem('birthDate') || '';

  const [birthDate, setBirthDate] = React.useState<string | undefined>(
    storedBirthDate || undefined,
  );

  const {
    register,
    handleSubmit,
    watch,
    formState: { errors, isValid },
  } = useForm<FormValues>({
    mode: 'onChange',
    defaultValues: {
      nickname: storedNickname,
      birthDate: storedBirthDate,
    },
  });

  const nickname = watch('nickname');
  React.useEffect(() => {
    if (nickname) {
      sessionStorage.setItem('nickname', nickname);
    }
  }, [nickname]);

  const onSubmit = (values: FormValues) => {
    if (!birthDate) return;

    socialProfileMutate(
      { nickname: values.nickname.trim(), birthDate },
      {
        onSuccess: () => router.replace('/invite'),
        onError: () => setOpen(true),
      },
    );
  };

  if (updating) return <Loader />;

  const validateNickname = (v: string) => {
    const trimmed = v.trim();

    if (trimmed.length < 2) return '닉네임은 2자 이상이어야 합니다.';
    if (trimmed.length > 10) return '닉네임은 10자 이하로 입력해주세요.';
    if (/^[^A-Za-z0-9가-힣ㄱ-ㅎㅏ-ㅣ]/.test(trimmed)) return '특수문자로 시작할 수 없습니다.';

    return true;
  };
  return (
    <div className="w-full h-full flex flex-col gap-3 items-center justify-center pb-[70px]">
      <Image src="/images/logo/day_logo.svg" alt="큐메이트" width={173} height={55} />

      <form onSubmit={handleSubmit(onSubmit)} className="flex flex-col gap-3 w-[295px]">
        <div>
          <input
            type="text"
            maxLength={10}
            placeholder="닉네임"
            className={`bg-secondary rounded-md text-text-secondary w-full py-2 pl-4 border ${
              errors.nickname ? 'border-red-400' : 'border-gray'
            }`}
            {...register('nickname', {
              required: '닉네임을 입력해주세요.',
              validate: validateNickname,
            })}
          />
          {errors.nickname && (
            <p className="text-red-500 text-12 mt-1">{errors.nickname.message}</p>
          )}
        </div>

        <DateSelectButton
          label="생년 월일"
          onSelect={(d) => setBirthDate(d ? d.split('T')[0] : undefined)}
        />

        <Button
          className="w-full mt-3"
          type="submit"
          variant="primary"
          disabled={!isValid || !birthDate}
        >
          입력하기
        </Button>
      </form>

      <NoticeModal
        open={open}
        setOpen={setOpen}
        title={
          <>
            정보를 저장하지 못했습니다. <br />
            잠시 후 다시 시도해주세요.
          </>
        }
        danger
      />
    </div>
  );
}
