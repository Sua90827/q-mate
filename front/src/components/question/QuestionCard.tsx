'use client';
import { useTodayQuestion } from '@/hooks/useQuestions';
import { useRouter } from 'next/navigation';
import React from 'react';

//TODO: api연동 후
// isLoading, isError 처리 필요
export default function QuestionCard() {
  const { data, isLoading, isError } = useTodayQuestion();
  const router = useRouter();

  return (
    <div
      className="w-[320px] h-[320px] shadow-md bg-secondary rounded-lg flex flex-col justify-center items-center hover:cursor-pointer"
      onClick={() => router.push(`/question/detail/${data?.question.questionId}`)}
    >
      <span className="text-16 font-extrabold text-theme-accent">TODAY’S QUESTION</span>
      {/* TODO: p태그에 api연동 후 question.text 사용 */}
      <p className="text-24">{data?.question.text}</p>
    </div>
  );
}
