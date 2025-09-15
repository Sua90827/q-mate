import React from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';

export default function Record() {
  return (
    <div className="w-full h-screen bg-gradient-sub flex items-center justify-center">
      <div className="w-[320px] h-[481px] flex  flex-col justify-center ">
        <div className="w-[320px] h-[320px] shadow-md bg-secondary rounded-lg flex flex-col justify-center items-center ">
          <span className=" text-primary text-lg">TODAY’S QUESTION</span>
          <p className="text-24">우리가 우주 여행을 간다면?</p>
        </div>
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
  );
}
