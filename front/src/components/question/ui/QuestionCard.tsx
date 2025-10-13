'use client';
import { Skeleton } from '@/components/ui/skeleton';
import { is } from 'date-fns/locale';
import { useRouter } from 'next/navigation';
import React from 'react';

type Props = {
  questionInstanceId?: number;
  questionText?: string;
  isLoading?: boolean;
};
export default function QuestionCard({ questionInstanceId, questionText, isLoading }: Props) {
  const router = useRouter();
  if (isLoading) return <Skeleton className="h-[320px] w-[320px]" />;
  return (
    <div
      className="w-[320px] h-[320px] shadow-md bg-secondary rounded-lg flex flex-col justify-center items-center hover:cursor-pointer px-2"
      onClick={() => router.push(`/question/detail?id=${questionInstanceId}`)}
    >
      <span className="text-16 font-extrabold text-theme-accent ">TODAY’S QUESTION</span>
      <p className="text-24">{questionText}</p>
    </div>
  );
}
