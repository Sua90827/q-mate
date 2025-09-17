'use client';
import { useRouter } from 'next/navigation';
import React from 'react';

//TODO: api연동 후 question.text
// ID값으로 question.questionId
export default function QuestionCard() {
  const router = useRouter();
  return (
    <div
      className="w-[320px] h-[320px] shadow-md bg-secondary rounded-lg flex flex-col justify-center items-center "
      // onClick={() => router.push(`/question/${questionId}`)}
    >
      <span className=" text-primary text-lg">TODAY’S QUESTION</span>
      {/*TODO: p태그에 api연동 후 question.title 사용 */}
      <p className="text-24">우리가 우주 여행을 간다면?</p>
      {/* <p className="text-24">{question.text}</p> */}
    </div>
  );
}
