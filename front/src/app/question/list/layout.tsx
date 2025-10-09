'use client';

import React from 'react';
import QuestionList from '@/components/question/QuestionList';

export default function QuestionListLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="flex h-full flex-row gap-4 sm:gap-6 md:gap-10 sm:mx-10 md:mx-[120px] justify-center items-center">
      {/* 좌측 리스트: 웹에서만 고정 */}
      <aside className="hidden md:block  shrink-0">
        <QuestionList />
      </aside>

      {/* 우측: 라우팅 children */}
      <section className="flex-1 h-full">{children}</section>
    </div>
  );
}
