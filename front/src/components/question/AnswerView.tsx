'use client';
import React, { useRef } from 'react';
import ShareBtn from './ui/ShareBtn';
import CloseButton from '../common/CloseButton';
import { useRouter } from 'next/navigation';
import { useSelectedStore } from '@/store/useSelectedStore';
import Link from 'next/link';

type Props = {
  questionText: string;
  myContent: string;
  partnerContent: string;
  nickname: string;
  partnerNickname: string;
  questionInstanceId: number;
};

function AnswerView({
  questionText,
  myContent,
  partnerContent,
  nickname,
  partnerNickname,
  questionInstanceId,
}: Props) {
  const captureID = `shareCard-${questionInstanceId}`;
  const cardRef = useRef<HTMLDivElement>(null);
  const router = useRouter();
  const setSelectedMenu = useSelectedStore((state) => state.setSelectedMenu);

  return (
    <>
      <div className="w-full relative top-0 h-[70px] flex justify-center items-center sm:hidden">
        <Link href="/main" onClick={() => setSelectedMenu('home')}>
          <span
            className="site-logo inline-block w-[109px] h-[35px]"
            role="img"
            aria-label="큐메이트"
          />
        </Link>
        <div className="absolute right-5 sm:hidden ">
          <CloseButton onClick={() => router.push('/question/list')} />
        </div>
      </div>
      <div className="pt-[70px] relative flex flex-col items-center w-full h-full sm:w-[400px] sm:h-[550px] bg-secondary sm:rounded-md shadow-md">
        <div ref={cardRef} id={captureID} className="w-full h-full flex flex-col p-10">
          <p className="text-text-secondary pt-5">#01</p>
          <h2 className="text-24 font-bold">{questionText}</h2>
          <div className="mt-16">
            <div className="pb-6">
              <p className="text-18">{nickname}</p>
              <p className="text-gray-500 text-16">{myContent}</p>
            </div>

            <div>
              <p className="text-18">{partnerNickname}</p>
              <p className="text-gray-500 text-16">{partnerContent}</p>
            </div>
          </div>

          <ShareBtn
            targetId={captureID}
            targetRef={cardRef}
            title={`${questionText} 답변`}
            text={`${nickname}: ${myContent}\n${partnerNickname}: ${partnerContent}`}
          />
        </div>
      </div>
    </>
  );
}

export default AnswerView;
