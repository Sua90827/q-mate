'use client';
import { useRouter } from 'next/navigation';
import React from 'react';

type Props = {
  questionInstanceId?: number;
  questionText?: string;
};
export default function QuestionCard({ questionInstanceId, questionText }: Props) {
  const router = useRouter();

  return (
    <div
      className="w-[320px] h-[320px] shadow-md bg-secondary rounded-lg flex flex-col justify-center items-center hover:cursor-pointer"
      onClick={() => router.push(`/question/detail?id=${questionInstanceId}`)}
    >
      <span className="text-16 font-extrabold text-theme-accent">TODAY’S QUESTION</span>
      <p className="text-24">{questionText}</p>
    </div>
  );
}
