'use client';
import React from 'react';
import { DatePicker } from './DatePicker';
import RepeatSelector from './ui/RepeatSelector';
import { Button } from '../common/Button';
import Link from 'next/link';

export default function CalendarRegister() {
  return (
    <div className="w-full h-screen bg-gradient-sub flex justify-center items-center">
      <form className="flex w-[400px]  flex-col gap-5" onSubmit={(e) => e.preventDefault()}>
        <DatePicker />
        <input
          type="text"
          placeholder="일정을 입력해주세요."
          className="bg-secondary border border-gray shadow-md rounded-md py-2 pl-3 w-full"
        />
        <textarea
          placeholder="설명을 입력해주세요"
          className="w-full h-[130px] bg-secondary border border-gray shadow-md rounded-md pt-3 pl-3 resize-none"
        />
        <RepeatSelector
          titleLabel="반복 선택"
          option1="반복 없음"
          option2="매년"
          option3="매달"
          option4="매주"
        />
        <RepeatSelector
          titleLabel="알림 선택"
          option1="알림없음"
          option2="일주일전"
          option3="3일전"
          option4="당일"
        />
        <div className="flex gap-10 mt-3">
          <Button variant="outline" size="lg" asChild className="w-[180px]">
            <Link href="/schedule">취소하기</Link>
          </Button>
          <Button size="lg" className="w-[180px]">
            등록하기
          </Button>
        </div>
      </form>
    </div>
  );
}
