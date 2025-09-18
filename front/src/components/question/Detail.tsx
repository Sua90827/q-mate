'use client';
import { useTodayQuestion } from '@/hooks/useQuestions';
import React from 'react';
import AnswerForm from './AnswerForm';
import AnswerView from './AnswerView';

export default function Detail() {
  const { data, isLoading, isError } = useTodayQuestion();

  if (isLoading) return <div>로딩중.</div>;
  if (isError || !data) return <div>에러</div>;

  const { status, question, myAnswer } = data;

  return (
    <>
      {status === 'PENDING' && myAnswer === null && (
        <AnswerForm mode="create" questionText={question.text} />
      )}
      {status === 'PENDING' && myAnswer !== null && (
        <AnswerForm mode="edit" questionText={question.text} />
      )}
      {/* {status === 'COMPLETED' && <AnswerView />} */}
    </>
  );
}
