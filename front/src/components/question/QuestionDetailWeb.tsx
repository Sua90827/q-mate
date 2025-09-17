import React from 'react';
import ShareBtn from './ui/ShareBtn';
import Custom from './Custom';
import { Answer, QuestionInstance } from '@/types/questionType';
import { useQuestionDetail } from '@/hooks/useQuestions';

export default function QuestionDetailWeb({ id, data }: { id: number; data: QuestionInstance[] }) {
  const customItem = data.find((q) => q.status === 'EDITABLE' && q.questionInstanceId === id);

  const { data: detail, isLoading, isError } = useQuestionDetail(id);

  if (isLoading) return <div>불러오는 중...</div>;
  if (isError) return <div>에러가 발생했습니다.</div>;

  if (customItem) {
    return <Custom value={customItem.question.text} />;
  }

  if (!detail) return <div>선택된 질문이 없습니다.</div>;

  const hasAnswer = detail.answers.some((a: Answer) => a.content && a.content.trim() !== '');

  return (
    <>
      {hasAnswer ? (
        <div className="relative p-10 flex flex-col items-center lg:w-[500px] md:w-[450px] w-[350px] h-[550px] bg-secondary rounded-md shadow-md">
          <p className="text-text-secondary pt-5">#01</p>
          <h2 className="text-24 font-bold">{detail.question.text}</h2>
          <div className="mt-16">
            {/* 내 답변 */}
            <div className="pb-6">
              <p className="text-18">조용한 유령</p>
              <p className="text-gray-500 text-16">
                {detail.answers.find((a) => a.isMine)?.content}
              </p>
            </div>

            {/* 상대방 답변 */}
            <div>
              <p className="text-18">활기찬 고래</p>
              <p className="text-gray-500 text-16">
                {detail.answers.find((a) => !a.isMine)?.content}
              </p>
            </div>

            <ShareBtn />
          </div>
        </div>
      ) : (
        <div className="relative p-10 flex flex-col items-center lg:w-[500px] md:w-[450px] w-[350px] h-[550px] bg-secondary rounded-md shadow-md">
          <p className="text-text-secondary pt-5">#01</p>
          <h2 className="text-24 font-bold">{detail.question.text}</h2>
          <Custom value={detail.question.text} />
        </div>
      )}
    </>
  );
}
