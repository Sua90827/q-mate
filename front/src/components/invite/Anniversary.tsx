'use client';
import Image from 'next/image';
import React, { useState, useEffect } from 'react';
import { DateSelectButton } from '../common/DateSelectButton';
import { useRouter } from 'next/navigation';

export default function Anniversary() {
  const [date, setDate] = useState<string | undefined>(undefined);
  const router = useRouter();

  useEffect(() => {
    if (date) {
      router.push(`/invite/COUPLE?date=${date}`);
    }
  }, [date, router]);

  return (
    <>
      <div className="mb-10 text-center">
        <p className="font-Gumi text-24">연인과 처음 만난 날을 </p>
        <p className="font-Gumi text-24">선택해주세요</p>
      </div>

      <Image src="/images/bubbley/bubbley_baby.png" alt="버블리 캐릭터" width={120} height={167} />

      <div className="w-[300px] mt-10">
        <DateSelectButton
          label="날짜 선택"
          onSelect={(d) => setDate(d ? d.split('T')[0] : undefined)}
          isAnniversary={true}
        />
      </div>
    </>
  );
}
