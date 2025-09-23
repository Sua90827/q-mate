import { Button } from '@/components/common/Button';
import Image from 'next/image';
import React from 'react';

export default function page() {
  return (
    <>
      <div className="mb-10 font-Gumi text-24 text-center">
        <p>버블리와 함께할 </p>
        <p>방법을 선택해 주세요</p>
      </div>
      <Image src="/images/bubbley/bubbley_baby.png" alt="버블리 캐릭터" width={120} height={167} />
      <div className="mt-10 flex flex-col gap-5">
        <Button variant="invite" className="w-[300px] z-10">
          초대하기
        </Button>
        <Button variant="primary" className="w-[300px] z-10">
          초대 코드 입력하기
        </Button>
      </div>
    </>
  );
}
