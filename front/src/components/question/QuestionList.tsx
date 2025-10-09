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
import PrevBtn from '../common/PrevBtn';
import NextBtn from '../common/NextBtn';
import { useMatchIdStore } from '@/store/useMatchIdStore';

export default function QuestionList() {
  const [queryText, setQueryText] = useState<string>('');
  const [showCustomOnly, setShowCustomOnly] = useState<boolean>(false);
  const [isDeleteMode, setIsDeleteMode] = useState<boolean>(false);
  const [page, setPage] = useState<number>(0);
  const pageSize = 20;
  const matchId = useMatchIdStore((state) => state.matchId);

  // API 호출
  const { data: questionResponse } = useQuestions(matchId!);
  const { data: customResponse } = useFetchCustomQuestions(matchId!);

  const questionInstances: QuestionList[] = useMemo(
    () => questionResponse?.content ?? [],
    [questionResponse],
  );
  const customQuestions = customResponse?.content ?? [];

  // 커스텀 질문 포맷팅
  const normalizedCustomInstances: QuestionList[] = customQuestions.map((custom) => ({
    questionInstanceId: `custom-${custom.customQuestionId}`,
    deliveredAt: custom.createdAt,
    status: custom.isEditable ? 'EDITABLE' : 'LOCKED',
    text: custom.text,
    completedAt: custom.updatedAt,
  }));

  // 질문 합치기
  const mergedInstances: QuestionList[] = useMemo(
    () => [...normalizedCustomInstances, ...questionInstances],
    [questionInstances, normalizedCustomInstances],
  );

  let filteredInstances = mergedInstances;

  // 검색
  if (queryText) {
    filteredInstances = mergedInstances.filter((instance) =>
      instance.text.toLowerCase().includes(queryText.toLowerCase()),
    );
  }

  // 커스텀 수정 가능 필터
  if (showCustomOnly) {
    filteredInstances = normalizedCustomInstances;
  }

  // 페이지네이션 처리
  const pagedInstances: QuestionList[] = useMemo(
    () => filteredInstances.slice(page * pageSize, (page + 1) * pageSize),
    [filteredInstances, page],
  );

  // 총 페이지 수
  const totalPages = Math.ceil(filteredInstances.length / pageSize);

  // 라우터
  const router = useRouter();
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const selectedQuestionInstanceIdParam = searchParams.get('id');
  const selectedQuestionInstanceId =
    selectedQuestionInstanceIdParam !== null ? String(selectedQuestionInstanceIdParam) : null;

  const { mutate: deleteCustomMutate } = useDeleteCustomQuestion();

  const handleDelete = (id: number | string) => {
    const targetId = String(id).replace('custom-', '');
    deleteCustomMutate(Number(targetId));
  };

  const openDetailByQuery = (targetQuestionInstanceId: number | string) => {
    const params = new URLSearchParams(searchParams);
    params.set('id', String(targetQuestionInstanceId));
    router.push(`${pathname}?${params.toString()}`);
  };

  return (
    <div className="w-full h-full flex items-center justify-center">
      {/* 모바일 */}
      <div className="sm:hidden w-full h-[calc(100vh-70px)] flex flex-col">
        <div className="flex justify-between items-center h-[70px] px-4 flex-shrink-0">
          <FilterBtn setShowCustomOnly={setShowCustomOnly} className="text-theme-primary" />
          <p className="text-20 font-Gumi text-theme-primary ">질문 리스트</p>
          <TrashCan onClick={() => setIsDeleteMode((prev) => !prev)} />
        </div>
        <div className="py-4 px-4 mb-[20px]">
          <SearchInput query={queryText} setQuery={setQueryText} />
        </div>
        <div className="bg-secondary flex flex-col flex-1">
          <ul className="flex-1 overflow-y-auto flex flex-col divide-y divide-dash border-t border-gray pb-[70px]">
            {pagedInstances.map((instance) => {
              const isSelected = selectedQuestionInstanceId === instance.questionInstanceId;

              let itemClassName =
                'text-16 py-7 cursor-pointer last:border-b last:border-dash last:border-gray';
              if (isSelected) itemClassName += ' font-bold bg-theme-list-active';
              if (instance.status === 'EDITABLE')
                itemClassName += ' text-text-secondary bg-gray font-bold';
              if (instance.status === 'PENDING') itemClassName += ' text-theme-accent2';

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
                    {isDeleteMode && instance.status === 'EDITABLE' && (
                      <DeleteBtn
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDelete(instance.questionInstanceId);
                        }}
                      />
                    )}
                  </div>
                </li>
              );
            })}
          </ul>
          {/* 페이지네이션 */}
          <div className="sticky bottom-[70px] flex justify-between items-center py-3 px-4 border-t border-gray bg-secondary">
            <PrevBtn page={page} setPage={setPage} />
            <div className="bg-calendar w-8 h-8 rounded-full flex justify-center items-center">
              <span>{page + 1}</span>
            </div>
            <NextBtn page={page} setPage={setPage} totalPages={totalPages} />
          </div>
        </div>
      </div>

      {/* 데스크탑 */}
      <div className="hidden sm:flex bg-secondary rounded-md shadow-md w-[320px] h-[550px] flex-col">
        <div className="mt-6 px-4 ">
          <SearchInput query={queryText} setQuery={setQueryText} />
        </div>
        <div className="flex justify-between items-center px-4 overflow-visible">
          <span className="inline-block text-20 font-bold py-4  select-none">질문 리스트</span>

          <FilterBtn setShowCustomOnly={setShowCustomOnly} />
        </div>
        <ul className="flex-1 overflow-y-auto flex flex-col divide-y divide-gray border-t border-gray">
          {pagedInstances.map((instance) => {
            const isSelected = selectedQuestionInstanceId === instance.questionInstanceId;

            let itemClassName =
              'py-5 cursor-pointer last:border-b last:border-gray last:border-solid';
            if (isSelected) itemClassName += ' bg-theme-list-active';
            if (instance.status === 'PENDING') itemClassName += ' text-theme-accent2';
            if (instance.status === 'EDITABLE')
              itemClassName += ' text-text-secondary bg-list-custom';

            return (
              <li
                key={instance.questionInstanceId}
                className={`${itemClassName} px-4`}
                onClick={() => openDetailByQuery(instance.questionInstanceId)}
              >
                <div className="flex justify-between">
                  {instance.text.length > 17 ? `${instance.text.slice(0, 16)}...` : instance.text}
                  {instance.status === 'EDITABLE' && (
                    <DeleteBtn
                      onClick={(e) => {
                        e.stopPropagation();
                        handleDelete(instance.questionInstanceId);
                      }}
                    />
                  )}
                </div>
              </li>
            );
          })}
        </ul>
        {/* 페이지네이션 */}
        <div className="sticky bottom-0 flex justify-between items-center py-3 px-4 border-t border-gray bg-secondary rounded-b-md">
          <PrevBtn page={page} setPage={setPage} />
          <div className="bg-calendar w-8 h-8 rounded-full flex justify-center items-center">
            <span>{page + 1}</span>
          </div>
          <NextBtn page={page} setPage={setPage} totalPages={totalPages} />
        </div>
      </div>
    </div>
  );
}
