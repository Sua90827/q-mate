'use client';
import React from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';

import BellBtn from '../common/BellBtn';
import QuestionCard from '../question/ui/QuestionCard';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useTodayQuestion } from '@/hooks/useQuestions';
import Loader from '../common/Loader';

export default function Record() {
  const matchId = useMatchIdStore((state) => state.matchId);

  const { data, isLoading, isError } = useTodayQuestion(matchId!);

  if (!matchId) return <Loader />;
  return (
    <div className="w-full h-full flex flex-col justify-center items-center sm:pt-0 pt-[70px]">
      {/* 모바일 상단바 */}
      <div className="fixed top-0 left-0 right-0 flex items-center justify-between py-5 sm:hidden">
        <div className="w-6" />
        <span className="absolute left-1/2 -translate-x-1/2 font-Gumi text-20 text-theme-primary">
          우리의 기록
        </span>
        <BellBtn />
      </div>
      <div className="w-full h-full flex items-center justify-center">
        <div className="w-[320px] h-[481px] flex flex-col justify-center ">
          <QuestionCard
            questionInstanceId={data?.questionInstanceId}
            questionText={data?.question.text}
            isLoading={isLoading}
          />
          <div className="pt-5 flex gap-6 ">
            <Button variant="outline" className="!w-[150px]">
              <Link href="/question/list">질문 리스트 보기</Link>
            </Button>
            <Button className="!w-[150px]">
              <Link href="/question/custom">질문 작성하기</Link>
            </Button>
          </div>
        </div>
      </div>
    </div>
  );
}
