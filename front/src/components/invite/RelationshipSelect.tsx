'use client';
import Image from 'next/image';
import React from 'react';
import { Button } from '../common/Button';
import { useRouter } from 'next/navigation';

export default function RelationshipSelect() {
  const router = useRouter();

  return (
    <>
      <div className="mb-10 font-Gumi text-24 text-center">
        <p>버블리와 함께 기록할 사람의</p>
        <p>관계를 골라주세요</p>
      </div>
      <Image src="/images/bubbley/bubbley_baby.png" alt="버블리 캐릭터" width={120} height={167} />
      <div className="mt-10 flex flex-col gap-5 ">
        <Button
          variant="invite"
          className="w-[300px] z-10"
          onClick={() => router.push('/invite/COUPLE/anniversary')}
        >
          연인
        </Button>

        <Button
          variant="invite"
          className="w-[300px] z-10"
          onClick={() => router.push('/invite/FRIEND')}
        >
          친구
        </Button>
      </div>
    </>
  );
}
