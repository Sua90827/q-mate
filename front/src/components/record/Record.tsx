import React from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';
import QuestionCard from '../question/QuestionCard';
import { BellIcon } from 'lucide-react';

export default function Record() {
  return (
    <>
      <div className="absolute top-0 left-0 right-0 flex items-center justify-between py-5 sm:hidden px-4">
        <div className="w-6" />
        <span className="flex-1 font-jalnan text-center text-20">우리의 기록</span>
        <Button size={'icon'} className="w-6 h-6 bg-transparent text-black">
          <BellIcon className="w-6 h-6 stroke-2" />
        </Button>
      </div>
      <div className="w-full h-screen bg-gradient-sub flex items-center justify-center">
        <div className="w-[320px] h-[481px] flex flex-col justify-center ">
          <QuestionCard />
          <div className="pt-5 flex gap-6 ">
            <Button variant="outline" size="lg" className="!w-[150px]">
              <Link href="/question/list">질문 리스트 보기</Link>
            </Button>
            <Button size="lg" className="!w-[150px]">
              <Link href="/question/custom ">질문 작성하기</Link>
            </Button>
          </div>
        </div>
      </div>
    </>
  );
}
