'use client';

import React, { useMemo, useState } from 'react';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import SearchInput from './ui/SearchInput';
import Filter from './ui/Filter';
import { X } from 'lucide-react';
import { useQuestions, useCustomQuestions } from '@/hooks/useQuestions';
import TrashCan from '../common/TrashCan';
import type { QuestionList } from '@/types/questionType';

export default function QuestionList() {
  const [queryText, setQueryText] = useState<string>('');
  const [showCustomOnly, setShowCustomOnly] = useState<boolean>(false);
  const [isDeleteMode, setIsDeleteMode] = useState<boolean>(false);

  // API 호출
  const { data: questionResponse } = useQuestions();
  const { data: customQuestions = [] } = useCustomQuestions();

  const questionInstances: QuestionList[] = useMemo(
    () => questionResponse?.[0]?.content ?? [],
    [questionResponse],
  );

  // 커스텀 질문을 QuestionList 포맷으로 변환
  const normalizedCustomInstances: QuestionList[] = customQuestions.map((custom) => ({
    questionInstanceId: -custom.customQuestionId, // id 충돌 방지
    deliveredAt: custom.createdAt,
    status: 'EDITABLE',
    question: { questionId: custom.customQuestionId, text: custom.text },
    completedAt: custom.updatedAt,
  }));

  // 질문 전체 합치기
  const totalInstances: QuestionList[] = useMemo(
    () => [...questionInstances, ...normalizedCustomInstances],
    [questionInstances, normalizedCustomInstances],
  );

  // 필터링
  const filteredInstances: QuestionList[] = totalInstances
    .filter((instance) => instance.question.text.toLowerCase().includes(queryText.toLowerCase()))
    .filter((instance) => (showCustomOnly ? instance.status === 'EDITABLE' : true));

  // 쿼리 파라미터 기반 선택 상태
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const selectedQuestionInstanceIdParam = searchParams.get('id');
  const selectedQuestionInstanceId =
    selectedQuestionInstanceIdParam !== null ? Number(selectedQuestionInstanceIdParam) : null;

  const openDetailByQuery = (targetQuestionInstanceId: number) => {
    const params = new URLSearchParams(searchParams as unknown as URLSearchParams);
    params.set('id', String(targetQuestionInstanceId));
    router.push(`${pathname}?${params.toString()}`);
  };

  return (
    <div className="w-full h-full">
      {/* 모바일 레이아웃 */}
      <div className="sm:hidden w-full h-full py-5">
        <div className="flex justify-between items-center h-[70px] px-4">
          <Filter setShowCustomOnly={setShowCustomOnly} />
          <p className="text-20 font-Gumi text-theme-primary">질문 리스트</p>
          <TrashCan onClick={() => setIsDeleteMode((prev) => !prev)} />
        </div>
        <div className="pt-10">
          <SearchInput query={queryText} setQuery={setQueryText} />
        </div>
        <ul className="mt-10 flex flex-col divide-y divide-text-secondary text-theme-primary border-y border-text-secondary overflow-y-auto">
          {filteredInstances.map((instance) => {
            const isSelected = selectedQuestionInstanceId === instance.questionInstanceId;
            const itemClassName = [
              'py-4 cursor-pointer',
              isSelected ? 'font-bold bg-theme-list-active' : '',
              instance.status === 'EDITABLE'
                ? 'text-text-secondary bg-gray font-bold'
                : instance.status === 'PENDING'
                ? 'text-theme-accent2'
                : '',
            ]
              .filter(Boolean)
              .join(' ');
            return (
              <li
                key={instance.questionInstanceId}
                className={`${itemClassName} px-4`}
                onClick={() => openDetailByQuery(instance.questionInstanceId)}
              >
                <div className="flex justify-between">
                  <p>
                    {instance.question.text.length > 17
                      ? `${instance.question.text.slice(0, 16)}...`
                      : instance.question.text}
                  </p>
                  {isDeleteMode && <X className="text-text-secondary mr-4 !w-4 !h-4" />}
                </div>
              </li>
            );
          })}
        </ul>
      </div>

      {/* 데스크탑 레이아웃 */}
      <div className="hidden sm:flex bg-secondary rounded-md shadow-md w-[320px] h-full flex-col">
        <div className="mt-6 px-4">
          <SearchInput query={queryText} setQuery={setQueryText} />
        </div>
        <div className="flex justify-between items-center px-4">
          <span className="inline-block text-20 font-bold py-4 cursor-default">질문 리스트</span>
          <Filter setShowCustomOnly={setShowCustomOnly} />
        </div>
        <ul className="flex flex-col divide-y divide-gray flex-1 overflow-y-auto">
          {filteredInstances.map((instance) => {
            const isSelected = selectedQuestionInstanceId === instance.questionInstanceId;
            const itemClassName = [
              'py-5 cursor-pointer',
              isSelected ? 'bg-theme-list-active' : '',
              instance.status === 'PENDING' ? 'text-theme-accent' : '',
              instance.status === 'EDITABLE' ? '!text-text-secondary bg-list-custom' : '',
            ]
              .filter(Boolean)
              .join(' ');
            return (
              <li
                key={instance.questionInstanceId}
                className={`${itemClassName} px-4`}
                onClick={() => openDetailByQuery(instance.questionInstanceId)}
              >
                <div className="flex justify-between">
                  {instance.question.text.length > 17
                    ? `${instance.question.text.slice(0, 16)}...`
                    : instance.question.text}
                  {instance.status === 'EDITABLE' && <X className="mx-4 !w-4 !h-4" />}
                </div>
              </li>
            );
          })}
        </ul>
      </div>
    </div>
  );
}
