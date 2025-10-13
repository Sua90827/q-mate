'use client';

import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import {
  useQuestionDetail,
  useAnswerQuestion,
  useUpdateAnswerQuestion,
  useRateQuestion,
} from '@/hooks/useQuestions';
import AnswerView from './AnswerView';
import Custom from './Custom';
import AnswerForm from './ui/AnswerForm';
import { Answer } from '@/types/questionType';
import { useFetchCustomQuestions } from '@/hooks/useCustom';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { Skeleton } from '../ui/skeleton';
import { SuccessToast, ErrorToast } from '../common/CustomToast';
import { useQueryClient } from '@tanstack/react-query';
import { useState } from 'react';
import RatingModal from './RatingModal';

export default function QuestionDetail() {
  const router = useRouter();
  const searchParams = useSearchParams();
  const idParam = searchParams.get('id');
  const isCustom = idParam?.startsWith('custom-');
  const questionInstanceId = idParam ? Number(idParam.replace('custom-', '')) : null;

  const [ratingOpen, setRatingOpen] = useState(false);
  const pathName = usePathname();
  const fromToday = pathName.startsWith('/question/detail');

  const matchId = useMatchIdStore((state) => state.matchId);
  const queryClient = useQueryClient();

  // 커스텀 질문 불러오기
  const { data } = useFetchCustomQuestions(matchId!);
  const customQuestions = data?.content ?? [];

  // 수정 가능한 커스텀 질문 찾기 (custom- 일 때만)
  const customItem = isCustom
    ? customQuestions.find((q) => q.customQuestionId === questionInstanceId)
    : null;

  // 커스텀일 때만 API 호출 막기
  const shouldFetch = questionInstanceId !== null && !isCustom;
  const {
    data: detail,
    isLoading,
    isError,
  } = useQuestionDetail(shouldFetch ? questionInstanceId! : undefined);

  const { mutateAsync: createAnswer, isPending: isCreating } = useAnswerQuestion();
  const { mutateAsync: updateAnswer, isPending: isUpdating } = useUpdateAnswerQuestion();
  const rateMutate = useRateQuestion();
  // 답변 생성 핸들러
  const handleCreateAnswer = async (content: string) => {
    if (!detail) return;
    try {
      await createAnswer({ questionInstanceId: detail.questionInstanceId, content });
      queryClient.invalidateQueries({ queryKey: ['pet'] });
      SuccessToast('답변을 제출했어요.');
    } catch {
      ErrorToast('답변 제출에 실패했어요. 잠시 후 다시 시도해 주세요.');
    }
  };

  // 답변 수정 핸들러
  const handleUpdateAnswer = async (content: string) => {
    if (!detail) return;
    const myAns = detail.answers.find((a: Answer) => a.isMine && a.answerId != null);
    if (!myAns?.answerId) {
      return handleCreateAnswer(content);
    }
    try {
      await updateAnswer({ answerId: myAns.answerId, content });
      SuccessToast('답변을 수정했어요.');
    } catch {
      ErrorToast('답변 수정에 실패했어요. 잠시 후 다시 시도해 주세요.');
    }
  };
  const handleRating = (questionId: number, isLike: boolean) => {
    if (!questionId) return;
    setRatingOpen(false);
    rateMutate.mutate(
      { questionId, isLike },
      {
        onSuccess: () => {
          SuccessToast('평가가 완료되었어요');
          router.push(fromToday ? '/record' : '/question/list');
        },
        onError: () => {
          ErrorToast('평가에 실패했어요.');
        },
      },
    );
  };

  // 질문 ID 없음
  if (questionInstanceId === null) {
    return (
      <div className="w-full h-full flex justify-center items-center">
        <div className="flex items-center justify-center w-full sm:w-[400px] h-full sm:h-[550px] bg-secondary/80 rounded-md shadow-md">
          <p className="text-24 opacity-80">선택된 질문이 없습니다.</p>
        </div>
      </div>
    );
  }

  // 커스텀 질문 처리
  if (customItem) {
    return (
      <div className="w-full h-full flex flex-col items-center">
        <Custom key={questionInstanceId} value={customItem.text} />
      </div>
    );
  }

  // 일반 질문 처리
  if (isLoading)
    return (
      <div className="w-full h-full flex justify-center items-center">
        <div className="flex items-center justify-center w-full sm:w-[400px] h-full sm:h-[550px] bg-secondary/80 rounded-md shadow-md">
          <Skeleton />
        </div>
      </div>
    );
  if (isError)
    return (
      <div className="w-full h-full flex justify-center items-center">
        <div className="flex items-center justify-center w-full sm:w-[400px] h-full sm:h-[550px] bg-secondary/80 rounded-md shadow-md">
          <p className="text-24 opacity-80">에러가 발생했습니다.</p>
        </div>
      </div>
    );
  if (!detail)
    return (
      <div className="w-full h-full flex justify-center items-center">
        <div className="flex items-center justify-center w-full sm:w-[400px] h-full sm:h-[550px] bg-secondary/80 rounded-md shadow-md">
          <p className="text-24 opacity-80">존재하지 않는 답변입니다.</p>
        </div>
      </div>
    );

  const my = detail.answers.find((answer: Answer) => answer.isMine);
  const partner = detail.answers.find((answer: Answer) => !answer.isMine);

  const hasMy = (my?.content ?? '').trim() !== '';
  const hasPartner = partner?.visible === true && (partner?.content ?? '').trim() !== '';

  return (
    <div className="w-full h-full justify-center flex flex-col items-center">
      <div className="w-full sm:w-[400px] h-full pb-[70px] sm:pb-0 sm:h-[550px]">
        {detail.status === 'PENDING' && hasMy ? (
          <AnswerForm
            key={`form-${detail.questionInstanceId}-edit`}
            questionId={detail.question.questionId}
            mode="edit"
            questionText={detail.question.text}
            onSubmit={handleUpdateAnswer}
            submitting={isUpdating}
            initialValue={my?.content}
          />
        ) : null}

        {detail.status === 'PENDING' && !hasMy ? (
          <AnswerForm
            key={`form-${detail.questionInstanceId}-create`}
            mode="create"
            questionId={detail.question.questionId}
            questionText={detail.question.text}
            onSubmit={handleCreateAnswer}
            submitting={isCreating}
          />
        ) : null}

        {detail.status === 'COMPLETED' || (hasMy && hasPartner) ? (
          <AnswerView
            questionInstanceId={detail.questionInstanceId}
            nickname={my?.nickname ?? ''}
            partnerNickname={partner?.nickname ?? ''}
            questionText={detail.question.text}
            myContent={my?.content ?? ''}
            partnerContent={partner?.content ?? ''}
          />
        ) : null}

        <RatingModal
          open={ratingOpen}
          onOpenChange={setRatingOpen}
          onLike={() => handleRating(detail?.question.questionId, true)}
          onDislike={() => handleRating(detail?.question.questionId, false)}
        />
      </div>
    </div>
  );
}
