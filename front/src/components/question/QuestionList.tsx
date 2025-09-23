'use client';

import React, { useMemo, useState } from 'react';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import SearchInput from './ui/SearchInput';
import Filter from './ui/Filter';
import { X } from 'lucide-react';
import { useQuestions, useCustomQuestions } from '@/hooks/useQuestions';
import { QuestionInstance } from '@/types/questionType';
import { useThemeStore } from '@/store/useThemeStore';
import TrashCan from '../common/TrashCan';

export default function QuestionList() {
  const [queryText, setQueryText] = useState<string>('');
  const [showCustomOnly, setShowCustomOnly] = useState<boolean>(false);
  const [isDeleteMode, setIsDeleteMode] = useState<boolean>(false);

  const { data: questionInstances = [] } = useQuestions();
  const { data: customQuestions = [] } = useCustomQuestions();

  // 커스텀 질문을 일반 질문리스트 포맷으로 정규화
  const normalizedCustomInstances: QuestionInstance[] = customQuestions.map((custom) => ({
    questionInstanceId: -custom.questionId,
    deliveredAt: custom.createdAt,
    status: 'EDITABLE',
    completedAt: '',
    question: { questionId: custom.questionId, text: custom.text },
  }));

  const totalInstances: QuestionInstance[] = useMemo(
    () => [...questionInstances, ...normalizedCustomInstances],
    [questionInstances, normalizedCustomInstances],
  );

  const filteredInstances: QuestionInstance[] = totalInstances
    .filter((instance) => instance.question.text.toLowerCase().includes(queryText.toLowerCase()))
    .filter((instance) => (showCustomOnly ? instance.status === 'EDITABLE' : true));

  const theme = useThemeStore((state) => state.theme);

  const activeBackgroundClass =
    theme === 'sunset' ? 'bg-sunset-list' : theme === 'night' ? 'bg-night-list' : 'bg-day-list';

  const pendingColorClass =
    theme === 'sunset'
      ? 'text-sunset-active'
      : theme === 'night'
      ? 'text-night-active2'
      : 'text-primary';

  const whiteTextClass = theme === 'night' ? 'text-secondary' : '';

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
          <p className={`text-20 font-Gumi ${whiteTextClass}`}>질문 리스트</p>
          <TrashCan onClick={() => setIsDeleteMode((prev) => !prev)} />
        </div>
        <div className="pt-10 px-4">
          <SearchInput query={queryText} setQuery={setQueryText} />
        </div>
        <ul className="mt-10 flex flex-col divide-y divide-gray border-y overflow-y-auto px-4">
          {filteredInstances.map((instance) => {
            const isSelected = selectedQuestionInstanceId === instance.questionInstanceId;
            const itemClassName = [
              'py-4 cursor-pointer',
              isSelected ? `font-bold ${activeBackgroundClass}` : '',
              instance.status === 'EDITABLE'
                ? 'text-text-secondary bg-gray font-bold'
                : instance.status === 'PENDING'
                ? pendingColorClass
                : theme === 'night'
                ? 'text-secondary'
                : '',
            ]
              .filter(Boolean)
              .join(' ');
            return (
              <li
                key={instance.questionInstanceId}
                className={itemClassName}
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
      <div className="mt-[70px] hidden sm:flex bg-secondary rounded-md shadow-md lg:w-[320px] w-[220px] h-[550px] flex-col">
        <div className="mt-6 px-4">
          <SearchInput query={queryText} setQuery={setQueryText} />
        </div>
        <div className="flex justify-between items-center px-4">
          <span className="inline-block text-20 font-bold py-4 cursor-default">질문 리스트</span>
          <Filter setShowCustomOnly={setShowCustomOnly} />
        </div>
        <ul className=" px-4 flex flex-col divide-y divide-gray flex-1 overflow-y-auto">
          {filteredInstances.map((instance) => {
            const isSelected = selectedQuestionInstanceId === instance.questionInstanceId;
            const itemClassName = [
              'py-5 cursor-pointer',
              isSelected ? activeBackgroundClass : '',
              instance.status === 'PENDING' ? pendingColorClass : '',
              instance.status === 'EDITABLE' ? '!text-text-secondary bg-list-custom' : '',
            ]
              .filter(Boolean)
              .join(' ');
            return (
              <li
                key={instance.questionInstanceId}
                className={itemClassName}
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
