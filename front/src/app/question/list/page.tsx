'use client';

import React from 'react';
import { useSearchParams } from 'next/navigation';
import QuestionList from '@/components/question/QuestionList';
import QuestionDetail from '@/components/question/QuestionDetail';

export default function QuestionListPage() {
  const searchParams = useSearchParams();
  const idParam = searchParams.get('id');

  return (
    <div className="w-full h-full flex flex-row items-center justify-center">
      <div className="md:hidden w-full h-full ">
        {idParam ? <QuestionDetail /> : <QuestionList />}
      </div>

      <div className="hidden md:flex flex-1 h-full">
        <QuestionDetail />
      </div>
    </div>
  );
}
