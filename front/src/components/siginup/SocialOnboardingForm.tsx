'use client';
import React, { useState } from 'react';
import TextInput from '../common/TextInput';
import { Button } from '../common/Button';
import Image from 'next/image';
import { DateSelectButton } from '../common/DateSelectButton';
import { useRouter } from 'next/navigation';
import { useSocialProfile } from '@/hooks/useAuth';
import Loader from '../common/Loader';
import NoticeModal from '../common/NoticeModal';

export default function SocialOnboardingForm() {
  const tempNickname = sessionStorage.getItem('nickname') || '';
  const [nickname, setNickname] = useState(tempNickname);
  const [isValid, setIsValid] = useState(false);
  const [date, setDate] = useState<string | undefined>(undefined);
  const [open, setOpen] = useState(false);
  const router = useRouter();
  const { mutate: socialProfileMutate, isPending: updating } = useSocialProfile();

  const handleSubmitProfile = () => {
    if (date && nickname) {
      socialProfileMutate(
        { nickname: nickname, birthDate: date },
        {
          onSuccess: () => {
            router.push('/invite');
          },
          onError: () => {
            setOpen(true);
          },
        },
      );
    }
  };

  if (updating) {
    <Loader />;
  }

  const validateNickname = (v: string) =>
    v.trim().length > 2 && v.trim().length <= 10 && /^[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]/.test(v);

  return (
    <div className=" w-full h-full flex flex-col gap-3 items-center justify-center pb-[70px]">
      <Image src="/images/logo/day_logo.svg" alt="큐메이트" width={173} height={55} />

      <form onSubmit={(e) => e.preventDefault()} className="flex flex-col gap-3">
        <TextInput
          label="닉네임"
          value={nickname}
          validate={validateNickname}
          setActive={setIsValid}
          onChange={setNickname}
        />
        <DateSelectButton
          label="생년 월일"
          onSelect={(d) => setDate(d ? d.split('T')[0] : undefined)}
        />
        <Button
          className="w-[295px] mt-3"
          disabled={!isValid}
          variant="primary"
          onClick={handleSubmitProfile}
        >
          입력하기
        </Button>
      </form>
      <NoticeModal
        open={open}
        setOpen={setOpen}
        title={
          <>
            정보 입력에 실패했습니다.
            <br />
            다시 시도해주세요.
          </>
        }
        danger
      />
    </div>
  );
}
