'use client';

import { useSearchParams, useRouter, usePathname } from 'next/navigation';
import { useQuestionDetail } from '@/hooks/useQuestions';

import AnswerView from './AnswerView';
import Custom from './Custom';
import AnswerForm from './ui/AnswerForm';
import CloseButton from '../common/CloseButton';
import { Answer } from '@/types/questionType';
import { useFetchCustomQuestions } from '@/hooks/useCustom';
import { SkeletonCard } from '../common/SkeletonCard';

export default function QuestionDetail() {
  const searchParams = useSearchParams();
  const idParam = searchParams.get('id');
  const questionInstanceId = idParam ? Number(idParam.replace('custom-', '')) : null;

  const router = useRouter();
  const pathname = usePathname();

  // 커스텀 질문 불러오기
  const { data } = useFetchCustomQuestions(1);
  const customQuestions = data?.content ?? [];

  // 수정 가능한 커스텀 질문 찾기
  const customItem = customQuestions.find(
    (q) => q.customQuestionId === questionInstanceId && q.isEditable === true,
  );

  //커스텀일때 API 호출막기
  const shouldFetch = questionInstanceId !== null && !customItem;
  const {
    data: detail,
    isLoading,
    isError,
  } = useQuestionDetail(shouldFetch ? questionInstanceId! : -1);

  // 질문 id 없음
  if (questionInstanceId === null) {
    return (
      <div className="w-full h-full flex justify-center  items-center">
        <div className="flex items-center justify-center  w-full sm:w-[400px] h-[550px] bg-secondary/80 rounded-md shadow-md">
          <p className="text-24 opacity-80">선택된 질문이 없습니다.</p>
        </div>
      </div>
    );
  }

  // 커스텀 질문 처리
  if (customItem) {
    return (
      <div className="w-full h-full relative p-6 flex flex-col items-center">
        <div className="absolute top-0 right-5 sm:hidden">
          <CloseButton onClick={() => router.push(pathname)} />
        </div>
        <Custom value={customItem.text} />
      </div>
    );
  }

  // 일반 질문 처리
  if (isLoading)
    return (
      <div className="w-full h-full flex justify-center  items-center">
        <div className="flex items-center justify-center  w-full sm:w-[400px] h-[550px] bg-secondary/80 rounded-md shadow-md"></div>
      </div>
    );
  if (isError)
    return (
      <div className="w-full h-full flex justify-center  items-center">
        <div className="flex items-center justify-center  w-full sm:w-[400px] h-[550px] bg-secondary/80 rounded-md shadow-md">
          <p className="text-24 opacity-80">에러가 발생했습니다.</p>
        </div>
      </div>
    );
  if (!detail)
    return (
      <div className="w-full h-full flex justify-center  items-center">
        <div className="flex items-center justify-center  w-full sm:w-[400px] h-[550px] bg-secondary/80 rounded-md shadow-md">
          <p className="text-24 opacity-80">존재하지 않는 답변입니다.</p>
        </div>
      </div>
    );

  const my = detail.answers.find((answer: Answer) => answer.isMine);
  const partner = detail.answers.find((answer: Answer) => !answer.isMine);

  const hasMy = (my?.content ?? '').trim() !== '';
  const hasPartner = partner?.visible === true && (partner?.content ?? '').trim() !== '';

  return (
    <div className="w-full h-[550px] justify-center flex flex-col items-center">
      <div className="h-[550px]">
        {detail.status === 'PENDING' && hasMy ? (
          <AnswerForm mode="edit" questionText={detail.question.text} />
        ) : null}

        {detail.status === 'PENDING' && !hasMy ? (
          <AnswerForm mode="create" questionText={detail.question.text} />
        ) : null}

        {detail.status === 'COMPLETED' || (hasMy && hasPartner) ? (
          <AnswerView
            nickname={my?.nickname ?? ''}
            partnerNickname={partner?.nickname ?? ''}
            questionText={detail.question.text}
            myContent={my?.content ?? ''}
            partnerContent={partner?.content ?? ''}
          />
        ) : null}
      </div>
    </div>
  );
}
