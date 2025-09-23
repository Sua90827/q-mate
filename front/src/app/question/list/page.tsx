// src/app/(with-tabs)/question/list/page.tsx
'use client';

import React from 'react';
import { useSearchParams } from 'next/navigation';
import QuestionList from '@/components/question/QuestionList';
import QuestionDetail from '@/components/question/QuestionDetail';

export default function QuestionListPage() {
  const searchParams = useSearchParams();
  const idParam = searchParams.get('id');

  return (
    <div className="w-full" suppressHydrationWarning>
      <div className="sm:hidden">{idParam ? <QuestionDetail /> : <QuestionList />}</div>

      <div className="hidden sm:flex flex-1">
        <QuestionDetail />
      </div>
    </div>
  );
}
