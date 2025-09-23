import Image from 'next/image';
import React from 'react';
import { DatePicker } from '../common/DatePicker';

export default function Anniversary() {
  return (
    <>
      <div className="mb-10 text-center">
        <p className="font-Gumi text-24">연인과 처음 만난 날을 </p>
        <p className="font-Gumi text-24">선택해주세요</p>
      </div>

      <Image src="/images/bubbley/bubbley_baby.png" alt="버블리 캐릭터" width={120} height={167} />

      <div className="w-[300px]  mt-10">
        <DatePicker label="날짜 선택" anniversary />
      </div>
    </>
  );
}
