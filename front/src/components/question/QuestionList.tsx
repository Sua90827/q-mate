'use client';

import React, { useMemo, useState } from 'react';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import SearchInput from './ui/SearchInput';
import { useQuestions } from '@/hooks/useQuestions';
import TrashCan from '../common/TrashCan';
import type { QuestionList } from '@/types/questionType';
import DeleteBtn from '../common/DeleteBtn';
import { useDeleteCustomQuestion, useFetchCustomQuestions } from '@/hooks/useCustom';
import FilterBtn from '../common/FilterBtn';

export default function QuestionList() {
  const [queryText, setQueryText] = useState<string>('');
  const [showCustomOnly, setShowCustomOnly] = useState<boolean>(false);
  const [isDeleteMode, setIsDeleteMode] = useState<boolean>(false);

  // API 호출
  const { data: questionResponse } = useQuestions(); //전체 질문 조회
  const { data } = useFetchCustomQuestions(1); //커스텀 질문

  const questionInstances: QuestionList[] = useMemo(
    () => questionResponse?.[0]?.content ?? [],
    [questionResponse],
  );

  const customQuestions = data?.content ?? [];

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

  const {
    mutate: deleteCustomMutate,
    isPending: isDeleting,
    isError: isDeleteError,
  } = useDeleteCustomQuestion();

  const handleDelete = async () => {
    const id = 153;
    deleteCustomMutate(id);
    if (isDeleting) {
      // 로딩 처리 가능
    } else if (isDeleteError) {
      // 에러 모달 예정
    }
  };

  return (
    <div className="w-full h-full">
      {/* 모바일 레이아웃 */}
      <div className="sm:hidden w-full h-[calc(100vh-70px)]">
        <div className="flex justify-between items-center h-[70px] px-4">
          <FilterBtn setShowCustomOnly={setShowCustomOnly} className="text-theme-primary" />
          <p className="text-20 font-Gumi text-theme-primary">질문 리스트</p>
          <TrashCan onClick={() => setIsDeleteMode((prev) => !prev)} />
        </div>
        <div className="py-10">
          <SearchInput query={queryText} setQuery={setQueryText} />
        </div>
        <div className="bg-secondary h-[calc(100vh-177px)]">
          <ul className="flex flex-col divide-y divide-text-secondary border-y border-text-secondary">
            {filteredInstances.map((instance) => {
              const isSelected = selectedQuestionInstanceId === instance.questionInstanceId;

              let itemClassName = 'text-16 py-7 cursor-pointer';
              if (isSelected) {
                itemClassName += ' font-bold bg-theme-list-active';
              }
              if (instance.status === 'EDITABLE') {
                itemClassName += ' text-text-secondary bg-gray font-bold';
              }
              if (instance.status === 'PENDING') {
                itemClassName += ' text-theme-accent2';
              }

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
                    {isDeleteMode && <DeleteBtn onClick={handleDelete} />}
                  </div>
                </li>
              );
            })}
          </ul>
        </div>
      </div>

      {/* 데스크탑 레이아웃 */}
      <div className="hidden sm:flex bg-secondary rounded-md shadow-md w-[320px] h-[550px] flex-col">
        <div className="mt-6 px-4">
          <SearchInput query={queryText} setQuery={setQueryText} />
        </div>
        <div className="flex justify-between items-center px-4">
          <span className="inline-block text-20 font-bold py-4 cursor-default">질문 리스트</span>
          <FilterBtn setShowCustomOnly={setShowCustomOnly} />
        </div>
        <ul className="flex flex-col divide-y divide-gray flex-1 overflow-y-auto border-y border-gray">
          {filteredInstances.map((instance) => {
            const isSelected = selectedQuestionInstanceId === instance.questionInstanceId;

            let itemClassName = 'py-5 cursor-pointer';
            if (isSelected) {
              itemClassName += ' bg-theme-list-active';
            }
            if (instance.status === 'PENDING') {
              itemClassName += ' text-theme-accent2';
            }
            if (instance.status === 'EDITABLE') {
              itemClassName += ' !text-text-secondary bg-list-custom';
            }

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
                  {instance.status === 'EDITABLE' && <DeleteBtn onClick={handleDelete} />}
                </div>
              </li>
            );
          })}
        </ul>
      </div>
    </div>
  );
}
