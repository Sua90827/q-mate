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
  const { data: questionResponse } = useQuestions(1);
  const { data } = useFetchCustomQuestions(1);

  const questionInstances: QuestionList[] = useMemo(
    () => questionResponse?.content ?? [],
    [questionResponse],
  );

  const customQuestions = data?.content ?? [];

  // 커스텀 질문을 QuestionList 포맷으로 변환
  const normalizedCustomInstances: QuestionList[] = customQuestions.map((custom) => ({
    questionInstanceId: `custom-${custom.customQuestionId}`,
    deliveredAt: custom.createdAt,
    status: custom.isEditable,
    text: custom.text,
    completedAt: custom.updatedAt,
  }));

  // 수정 가능한 질문만 빼기
  const editableCustom = normalizedCustomInstances.filter((instance) => instance.status === true);

  // 질문 전체 합치기
  const totalInstances: QuestionList[] = useMemo(
    () => [...questionInstances, ...editableCustom],
    [questionInstances, editableCustom],
  );

  let filteredInstances = totalInstances;

  // 검색
  if (queryText) {
    filteredInstances = totalInstances.filter((instance) =>
      instance.text.toLowerCase().includes(queryText.toLowerCase()),
    );
  }

  // 커스텀 질문만 보기
  if (showCustomOnly) {
    filteredInstances = editableCustom;
  }

  // 라우터 관련 훅
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();

  // 현재 URL 쿼리 파라미터에서 'id' 꺼내기
  const selectedQuestionInstanceIdParam = searchParams.get('id');
  const selectedQuestionInstanceId =
    selectedQuestionInstanceIdParam !== null ? String(selectedQuestionInstanceIdParam) : null;

  const {
    mutate: deleteCustomMutate,
    isPending: isDeleting,
    isError: isDeleteError,
  } = useDeleteCustomQuestion();

  // 삭제 함수 (id 직접 받기)
  const handleDelete = (id: number | string) => {
    const targetId = String(id).replace('custom-', '');
    deleteCustomMutate(Number(targetId));
    if (isDeleting) {
      // 로딩 처리
    } else if (isDeleteError) {
      // 에러 처리
    }
  };

  // 특정 질문 클릭 시 URL에 id 쿼리 세팅
  const openDetailByQuery = (targetQuestionInstanceId: number | string) => {
    const params = new URLSearchParams(searchParams);
    params.set('id', String(targetQuestionInstanceId));
    router.push(`${pathname}?${params.toString()}`);
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
              if (instance.status === true) {
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
                      {instance.text.length > 17
                        ? `${instance.text.slice(0, 16)}...`
                        : instance.text}
                    </p>
                    {isDeleteMode && instance.status === true && (
                      <DeleteBtn
                        onClick={(e) => {
                          e.stopPropagation(); //부모 이벤트 전파 막기
                          handleDelete(instance.questionInstanceId);
                        }}
                      />
                    )}
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
            if (instance.status === true) {
              itemClassName += ' text-text-secondary bg-list-custom';
            }

            return (
              <li
                key={instance.questionInstanceId}
                className={`${itemClassName} px-4`}
                onClick={() => openDetailByQuery(instance.questionInstanceId)}
              >
                <div className="flex justify-between">
                  {instance.text.length > 17 ? `${instance.text.slice(0, 16)}...` : instance.text}
                  {instance.status === true && (
                    <DeleteBtn
                      onClick={(e) => {
                        e.stopPropagation(); //부모 이벤트 전파 막기
                        handleDelete(instance.questionInstanceId);
                      }}
                    />
                  )}
                </div>
              </li>
            );
          })}
        </ul>
      </div>
    </div>
  );
}
