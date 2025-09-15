import React from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';

export default function Custom() {
  return (
    <div className="flex items-center justify-center h-screen bg-gradient-sub">
      <div className="flex flex-col h-[246px]">
        <span className="font-bold text-[24px]  pb-5">궁금한 질문 작성하기</span>
        <textarea
          placeholder="내용을 입력해주세요"
          className="md:w-[600px] w-[310px]  h-[175px] rounded-md shadow-md p-3 bg-secondary border-border-gray"
        />
        <div className="pt-5 flex gap-7  justify-end">
          <Button variant="outline" size="lg" asChild>
            <Link href="/record">취소하기</Link>
          </Button>
          <Button size="lg">등록하기</Button>
        </div>
      </div>
    </div>
  );
}
