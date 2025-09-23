'use client';
import React, { useState } from 'react';
import Image from 'next/image';
import { Button } from '../common/Button';
import InvitedCheckModal from './ui/InvitedCheckModal';

export default function Invited() {
  const [isOpen, setIsOpen] = useState(false);

  return (
    <>
      <div className="mb-10 text-center">
        <p className="font-Gumi text-24">초대 코드를</p>
        <p className="font-Gumi text-24">등록해주세요</p>
        <input
          type="text"
          placeholder="초대 코드 입력"
          className="rounded-md pl-4 bg-secondary font-Pre text-14 py-4 mt-7 w-[250px]"
        />
      </div>

      <Image src="/images/bubbley/ bubbley_baby.png" alt="버블리 캐릭터" width={120} height={167} />
      <Button variant="invite" className="w-[300px] mt-10 z-10" onClick={() => setIsOpen(true)}>
        등록하기
      </Button>
      <InvitedCheckModal open={isOpen} setIsOpen={setIsOpen} />
    </>
  );
}
