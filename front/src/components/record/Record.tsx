'use client';
import React from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';
import QuestionCard from '../question/QuestionCard';
import BellBtn from '../common/BellBtn';

export default function Record() {
  return (
    <>
      {/* 모바일 상단 바 */}
      <div className="fixed top-0 left-0 right-0 flex items-center justify-between py-5 sm:hidden">
        <div className="w-6" />
        <span className="absolute left-1/2 -translate-x-1/2 font-Gumi text-20 text-theme-primary">
          우리의 기록
        </span>
        <BellBtn />
      </div>

      {/* 메인 콘텐츠 */}
      <div className="w-full h-full flex items-center justify-center">
        <div className="w-[320px] h-[481px] flex flex-col justify-center">
          <QuestionCard />
          <div className="pt-5 flex gap-6">
            <Button variant="outline" size="lg" className="!w-[150px]">
              <Link href="/question/list">질문 리스트 보기</Link>
            </Button>
            <Button size="lg" className="!w-[150px]">
              <Link href="/question/custom">질문 작성하기</Link>
            </Button>
          </div>
        </div>
      </div>
    </>
  );
}
