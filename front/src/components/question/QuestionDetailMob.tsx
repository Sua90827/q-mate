'use client';
import React from 'react';
import ShareBtn from './ui/ShareBtn';
import { useParams } from 'next/navigation';
import { X } from 'lucide-react';
import Link from 'next/link';
import { useQuestionDetail, useCustomQuestions } from '@/hooks/useQuestions';
import Custom from './Custom';

export default function QuestionDetailMob() {
  const { id } = useParams<{ id: string }>();
  const questionId = Number(id);

  const { data: item, isLoading, isError } = useQuestionDetail(questionId);

  const { data: customData = [] } = useCustomQuestions();
  const customItem = customData.find((q) => -q.questionId === questionId);

  if (customItem) {
    return (
      <div className="w-full h-screen bg-gradient-sub relative p-10 flex flex-col items-center md:hidden">
        <Link href={'/question/list'}>
          <X className="absolute right-3 top-3 !w-4 !h-4" />
        </Link>
        <p className="text-text-secondary pt-10">#01</p>
        <Custom value={customItem.text} />
      </div>
    );
  }

  if (isLoading) return <div className="p-6">불러오는 중...</div>;
  if (isError) return <div className="p-6">에러가 발생했습니다.</div>;
  if (!item) return <div className="p-6">선택된 질문이 없습니다.</div>;

  return (
    <div className="w-full h-screen bg-gradient-sub relative p-10 flex flex-col items-center">
      <Link href={'/question/list'}>
        <X className="absolute right-3 top-3" />
      </Link>

      {item.answers.length === 2 && (
        <>
          <p className="text-text-secondary pt-10">#01</p>
          <h2 className="text-24 font-bold">{item.question.text}</h2>

          <div className="mt-10">
            {/* 내 답변 */}
            <div className="pb-6">
              <p className="text-18">조용한 유령</p>
              <p className="text-gray-500 text-16">{item.answers.find((a) => a.isMine)?.content}</p>
            </div>

            {/* 상대방 답변 */}
            <div>
              <p className="text-18">활기찬 고래</p>
              <p className="text-gray-500 text-16">
                {item.answers.find((a) => !a.isMine)?.content}
              </p>
            </div>
          </div>

          <ShareBtn />
        </>
      )}
    </div>
  );
}
