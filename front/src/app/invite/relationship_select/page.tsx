import { Button } from '@/components/common/Button';
import Image from 'next/image';
import Link from 'next/link';
import React from 'react';

export default function page() {
  return (
    <>
      <div className="mb-10 font-Gumi text-24 text-center">
        <p>버블리와 함께 기록할 사람의</p>
        <p>관계를 골라주세요</p>
      </div>
      <Image src="/images/bubbley/bubbley_baby.png" alt="버블리 캐릭터" width={120} height={167} />
      <div className="mt-10 flex flex-col gap-5">
        <Button variant="invite" className="w-[300px] z-10" asChild>
          <Link href="/invite/anniversary">연인</Link>
        </Button>
        <Button variant="invite" className="w-[300px] z-10">
          <Link href="/invite/invite">친구</Link>
        </Button>
      </div>
    </>
  );
}
