'use client';
import React, { useState } from 'react';
import TextInput from '../common/TextInput';
import { Button } from '../common/Button';
import Image from 'next/image';
import { DateSelectButton } from '../common/DateSelectButton';

export default function SocialOnboardingForm() {
  const [nickname, setNickname] = useState('');
  const [isValid, setIsValid] = useState(false);

  const validateNickname = (v: string) =>
    v.trim().length > 0 && /^[A-Za-z0-9가-힣][A-Za-z0-9가-힣._-]{1,9}$/.test(v);

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
        <DateSelectButton label="생년 월일" />
        <Button className="w-[295px] mt-3" disabled={!isValid} variant="primary">
          입력하기
        </Button>
      </form>
    </div>
  );
}
