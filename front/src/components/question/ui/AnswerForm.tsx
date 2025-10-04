'use client';
import Image from 'next/image';
import Link from 'next/link';
import React, { useState } from 'react';

import RatingModal from '../RatingModal';
import { Button } from '@/components/common/Button';
import CloseButton from '@/components/common/CloseButton';
import { useRouter } from 'next/navigation';

type AnswerFormProps = {
  mode: 'create' | 'edit';
  questionText: string;
};

export default function AnswerForm({ questionText, mode }: AnswerFormProps) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [text, setText] = useState('');
  const router = useRouter();

  return (
    <>
      <div className="w-full relative top-0 h-[70px] flex justify-center items-center sm:hidden">
        <img alt="큐메이트" width={109} height={35} className="site-logo" />
        <div className="absolute  top-5 right-5 sm:hidden ">
          <CloseButton onClick={() => router.push('/question/list')} />
        </div>
      </div>
      <div className="flex flex-col items-center justify-center h-full bg-gradient-sub gap-10">
        <div className="flex flex-col h-[246px] gap-8">
          <span className="font-bold text-24 text-center pb-5 text-theme-primary">
            {questionText}
          </span>
          <div className="relative">
            <textarea
              placeholder="오늘의 질문에 답변을 해보세요!"
              value={text}
              onChange={(e) => setText(e.target.value)}
              maxLength={99}
              className="md:w-[400px] w-[310px] h-[175px] rounded-md shadow-md p-3 bg-secondary border border-gray text-[14px] resize-none"
            />
            <span className="text-text-secondary absolute right-3 bottom-4">{text.length}/100</span>
          </div>
          <div className="w-full flex justify-center md:justify-end">
            <Button
              size={'lg'}
              className="w-[310px] md:w-[200px]"
              onClick={() => setIsModalOpen(true)}
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
