import React from 'react';
import Custom from './Custom';
import { Answer, QuestionInstance } from '@/types/questionType';
import { useQuestionDetail } from '@/hooks/useQuestions';
import AnswerForm from './AnswerForm';
import AnswerView from './AnswerView';

export default function QuestionDetailWeb({ id, data }: { id: number; data: QuestionInstance[] }) {
  const customItem = data.find((q) => q.status === 'EDITABLE' && q.questionInstanceId === id);

  const { data: detail, isLoading, isError } = useQuestionDetail(id);

  if (isLoading) return <div>불러오는 중...</div>;
  if (isError) return <div>에러가 발생했습니다.</div>;

  if (customItem) {
    return <Custom value={customItem.question.text} />;
  }

  if (!detail) return <div>선택된 질문이 없습니다.</div>;

  const my = detail.answers.find((a: Answer) => a.isMine);
  const partner = detail.answers.find((a: Answer) => !a.isMine);

  const hasMy = my?.content?.trim() !== '';
  const hasPartner = partner?.visible === true && partner?.content?.trim() !== '';

  return (
    <>
      {/* PENDING이고 내 답변이 존재(상대방/미답변) */}
      {detail.status === 'PENDING' && hasMy ? (
        <AnswerForm
          mode="edit"
          questionText={detail.question.text}

          // TODO: onUpdate 구현 (PUT /answers/{answerId})
        />
      ) : null}

      {/* PENDING이고 내 답변이 없음 */}
      {detail.status === 'PENDING' && !hasMy ? (
        <AnswerForm
          mode="create"
          questionText={detail.question.text}
          // TODO: onCreate 구현 (POST /answers)
        />
      ) : null}
      {/* COMPLETED 또는 양측 답변 존재 */}
      {detail.status === 'COMPLETED' || (hasMy && hasPartner) ? (
        <AnswerView
          questionText={detail.question.text}
          myContent={my?.content ?? ''}
          partnerContent={partner?.content ?? ''}
        />
      ) : null}
    </>
  );
}
