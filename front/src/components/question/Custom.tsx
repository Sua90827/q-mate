'use client';
import React, { useState } from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';
import Image from 'next/image';

export default function Custom({ value }: { value?: string }) {
  const [text, setText] = useState(value ?? '');

  return (
    <>
      <div className="flex justify-center  ">
        <Link href="/main" className="absolute py-5 block sm:hidden">
          <Image src="/logo.svg" alt="큐메이트" width={94} height={30} />
        </Link>
      </div>
      <div className="flex items-center justify-center h-screen bg-gradient-sub">
        <div className="flex flex-col h-[246px]">
          <span className="font-bold text-[24px] pb-5">궁금한 질문 작성하기</span>
          <textarea
            placeholder="내용을 입력해주세요"
            value={text}
            onChange={(e) => setText(e.target.value)}
            className="md:w-[600px] w-[310px] h-[175px] rounded-md shadow-md p-3 bg-secondary border border-gray"
          />
          <div className="pt-5 flex gap-7 justify-end">
            <Button variant="outline" size="lg" asChild>
              <Link href="/record">취소하기</Link>
            </Button>
            {value ? <Button size="lg">수정하기</Button> : <Button size="lg">등록하기</Button>}
          </div>
        </div>
      </div>
    </>
  );
}
