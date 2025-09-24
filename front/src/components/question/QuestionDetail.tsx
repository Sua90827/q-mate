'use client';

import { useSearchParams, useRouter, usePathname } from 'next/navigation';
import { useQuestionDetail } from '@/hooks/useQuestions';

import AnswerView from './AnswerView';
import Custom from './Custom';
import AnswerForm from './ui/AnswerForm';
import CloseButton from '../common/CloseButton';
import { Answer } from '@/types/questionType';
import { useFetchCustomQuestions } from '@/hooks/useCustom';

export default function QuestionDetail() {
  const searchParams = useSearchParams();
  const idParam = searchParams.get('id');
  const questionInstanceId = idParam ? Number(idParam) : null;

  const router = useRouter();
  const pathname = usePathname();

  const { data: customQuestions = [] } = useFetchCustomQuestions();

  const customQuestionId =
    questionInstanceId !== null && questionInstanceId < 0 ? Math.abs(questionInstanceId) : null;

  const customItem = customQuestionId
    ? customQuestions.find((q) => q.customQuestionId === customQuestionId)
    : null;

  const isCustom = Boolean(customItem);

  const numericIdForHook =
    questionInstanceId === null || isCustom ? -1 : (questionInstanceId as number);

  const { data: detail, isLoading, isError } = useQuestionDetail(numericIdForHook);

  if (questionInstanceId === null) {
    return <div className="p-6">선택된 질문이 없습니다.</div>;
  }

  if (isCustom && customItem) {
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
  if (isLoading) return <div className="p-6">불러오는 중...</div>;
  if (isError) return <div className="p-6">에러가 발생했습니다.</div>;
  if (!detail) return <div className="p-6">선택된 질문이 없습니다.</div>;

  const my = detail.answers.find((answer: Answer) => answer.isMine);
  const partner = detail.answers.find((answer: Answer) => !answer.isMine);

  const hasMy = (my?.content ?? '').trim() !== '';
  const hasPartner = partner?.visible === true && (partner?.content ?? '').trim() !== '';

  return (
    <div className="w-full h-[550px] justify-center flex flex-col items-center">
      {/* <CloseButton onClick={() => router.push(pathname)} /> */}
      <div className="h-[550px]">
        {detail.status === 'PENDING' && hasMy ? (
          <AnswerForm mode="edit" questionText={detail.question.text} />
        ) : null}

        {detail.status === 'PENDING' && !hasMy ? (
          <AnswerForm mode="create" questionText={detail.question.text} />
        ) : null}

        {detail.status === 'COMPLETED' || (hasMy && hasPartner) ? (
          <AnswerView
            questionText={detail.question.text}
            myContent={my?.content ?? ''}
            partnerContent={partner?.content ?? ''}
          />
        ) : null}
      </div>
    </div>
  );
}
