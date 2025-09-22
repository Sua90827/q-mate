'use client';
import Image from 'next/image';
import Link from 'next/link';
import React, { useState } from 'react';
import { Button } from '../common/Button';
import RatingModal from './RatingModal';
import { useThemeStore } from '@/store/useThemeStore';

type AnswerFormProps = {
  mode: 'create' | 'edit';
  questionText: string;
};
export default function AnswerForm({ questionText, mode }: AnswerFormProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const theme = useThemeStore((state) => state.theme);

  return (
    <>
      <div className="flex justify-center">
        <Link href="/main" className="absolute py-5 block sm:hidden">
          <Image src="/images/logo/day_logo.svg" alt="큐메이트" width={94} height={30} />
        </Link>
      </div>
      <div className="flex flex-col items-center justify-center h-full bg-gradient-sub gap-10">
        <div className="flex flex-col h-[246px] gap-8">
          <span className="font-bold text-24 text-center pb-5">{questionText}</span>
          <textarea
            placeholder="오늘의 질문에 답변을 해보세요!"
            className="md:w-[600px] w-[310px] h-[175px] rounded-md shadow-md p-3 bg-secondary border border-gray text-[14px]"
          />
          <div className="w-full flex justify-center md:justify-end">
            <Button
              size={'lg'}
              className="w-[310px] sm:w-[200px]"
              onClick={() => setIsModalOpen(true)}
              theme={theme}
            >
              {mode === 'create' ? '답변하기' : '수정하기'}
            </Button>
          </div>
          <RatingModal
            open={isModalOpen}
            //onLike, onDislike 전송 api추가 필요
            onLike={() => setIsModalOpen(false)}
            onDislike={() => setIsModalOpen(false)}
          />
        </div>
      </div>
    </>
  );
}
