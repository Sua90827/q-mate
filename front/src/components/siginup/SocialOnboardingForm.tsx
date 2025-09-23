'use client';
import React, { useState } from 'react';
import TextInput from '../common/TextInput';
import { Button } from '../common/Button';
import Image from 'next/image';
import { DatePicker } from '../common/DatePicker';

export default function SocialOnboardingForm() {
  const [active, setActive] = useState(false);

  return (
    <div className=" w-full h-full flex flex-col gap-3 items-center justify-center ">
      <Image src="/images/logo/day_logo.svg" alt="큐메이트" width={173} height={55} />

      <form onSubmit={(e) => e.preventDefault()} className="flex flex-col gap-3">
        <TextInput label="닉네임" setActive={setActive} />
        <DatePicker label="생년 월일" />
        <Button className="w-[295px] mt-3" disabled={active ? false : true}>
          입력하기
        </Button>
      </form>
    </div>
  );
}
