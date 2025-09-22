'use client';
import React, { useMemo, useState } from 'react';
import SearchInput from './ui/SearchInput';
import { X } from 'lucide-react';
import Link from 'next/link';
import Filter from './ui/Filter';
import { useQuestions, useCustomQuestions } from '@/hooks/useQuestions';
import { QuestionInstance } from '@/types/questionType';
import TrashCan from '../common/TrashCan';

export default function QuestionListMob() {
  const [query, setQuery] = useState('');
  const [active, setActive] = useState<number | null>(null);
  const [showCustomOnly, setShowCustomOnly] = useState(false);
  const [isDelete, setIsDelete] = useState(false);

  const { data: questions = [] } = useQuestions();
  const { data: customData = [] } = useCustomQuestions();

  const normalizedCustomData: QuestionInstance[] = customData.map((c) => ({
    questionInstanceId: -c.questionId,
    deliveredAt: c.createdAt,
    status: 'EDITABLE',
    completedAt: '',
    question: { questionId: c.questionId, text: c.text },
  }));

  const totalData = useMemo(
    () => [...questions, ...normalizedCustomData],
    [questions, normalizedCustomData],
  );

  const filtered = totalData
    .filter((list) => list.question.text.toLowerCase().includes(query.toLowerCase()))
    .filter((list) => (showCustomOnly ? list.status === 'EDITABLE' : true));

  return (
    <div className="w-full h-full py-5">
      {/* 상단 헤더 */}
      <div className="flex justify-between items-center h-[70px] px-5">
        <Filter setShowCustomOnly={setShowCustomOnly} />
        <p className="text-20 font-Gumi text-theme-primary">질문 리스트</p>
        <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
      </div>

      {/* 검색창 */}
      <div className="pt-10">
        <SearchInput query={query} setQuery={setQuery} />
      </div>

      {/* 리스트 */}
      <ul className="mt-10 flex flex-col divide-y divide-text-secondary border-y  border-text-secondary overflow-y-auto">
        {filtered.map((list) => (
          <li
            key={list.questionInstanceId}
            className={`
              py-4 pl-5 cursor-pointer
              ${active === list.questionInstanceId ? 'font-bold bg-theme-list-active' : ''}
              ${list.status === 'EDITABLE' ? 'text-text-secondary bg-list-custom font-bold' : ''}
              ${list.status === 'PENDING' ? 'text-theme-accent' : ''}
              ${list.status === 'COMPLETED' ? 'text-theme-primary' : ''}
            `}
          >
            <Link
              href={`/question/list/detail/${list.questionInstanceId}`}
              onClick={() => setActive(list.questionInstanceId)}
              className="flex justify-between"
            >
              <p>
                {list.question.text.length > 17
                  ? list.question.text.slice(0, 16) + '...'
                  : list.question.text}
              </p>
              {isDelete && <X className="text-text-secondary mr-4 !w-4 !h-4" />}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
