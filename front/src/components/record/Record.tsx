'use client';
import React from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';
import QuestionCard from '../question/QuestionCard';
import { useThemeStore } from '@/store/useThemeStore';
import BellBtn from '../common/BellBtn';

export default function Record() {
  const theme = useThemeStore((state) => state.theme);
  let whiteClass = '';
  if (theme === 'night') whiteClass = 'text-secondary';

  return (
    <>
      <div className="fixed top-0 left-0 right-0 flex items-center justify-between py-5 sm:hidden ">
        <div className="w-6" />
        <span className={`flex-1 font-Gumi flex justify-center text-20 ${whiteClass}`}>
          우리의 기록
        </span>
        <BellBtn />
      </div>
      <div className="w-full min-h-screen flex items-center justify-center">
        <div className="w-[320px] h-[481px] flex flex-col justify-center ">
          <QuestionCard />
          <div className="pt-5 flex gap-6 ">
            <Button theme={theme} variant="outline" size="lg" className="!w-[150px]">
              <Link href="/question/list">질문 리스트 보기</Link>
            </Button>
            <Button theme={theme} size="lg" className="!w-[150px]">
              <Link href="/question/custom ">질문 작성하기</Link>
            </Button>
          </div>
        </div>
      </div>
    </>
  );
}
