import { Button } from '@/components/common/Button';
import InviteLayout from '@/components/invite/InviteLayout';
import Image from 'next/image';
import React from 'react';

export default function page() {
  return (
    <>
      <InviteLayout>
        <div className="mb-10 text-center">
          <p className="font-Gumi text-24">초대 코드를</p>
          <p className="font-Gumi text-24">등록해주세요</p>
          <input
            type="text"
            placeholder="초대 코드 입력"
            className="rounded-md pl-4 bg-secondary font-Pre text-16 py-2 mt-5"
          />
        </div>
        <Image src="/images/bubbley_baby.png" alt="버블리 캐릭터" width={120} height={167} />
        <Button variant="invite" className="w-[300px] mt-10 z-10">
          등록하기
        </Button>
      </InviteLayout>
    </>
  );
}
