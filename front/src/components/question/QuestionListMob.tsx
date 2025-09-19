'use client';
import React, { useMemo, useState } from 'react';
import SearchInput from './ui/SearchInput';
import { Trash2, X } from 'lucide-react';
import Link from 'next/link';
import Filter from './ui/Filter';
import { useQuestions, useCustomQuestions } from '@/hooks/useQuestions';
import { QuestionInstance } from '@/types/questionType';

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
    <div className="bg-gradient-sub w-full min-h-screen py-5">
      <div className="flex justify-between items-center h-[70px]">
        <Filter setShowCustomOnly={setShowCustomOnly} />
        <p className="text-20 font-Gumi">질문 리스트</p>
        <Trash2 className="!w-[24px] !h-[24px]" onClick={() => setIsDelete((prev) => !prev)} />
      </div>

      <div className="pt-10">
        <SearchInput query={query} setQuery={setQuery} />
      </div>

      <ul className="mt-10 flex flex-col divide-y divide-gray border-y overflow-y-auto">
        {filtered.map((list) => (
          <li
            key={list.questionInstanceId}
            className={`py-4 pl-5 cursor-pointer ${
              active === list.questionInstanceId ? 'bg-list-active font-bold' : ''
            } ${list.status === 'PENDING' ? 'text-primary font-bold' : ''} ${
              list.status === 'EDITABLE' ? 'text-text-secondary bg-gray font-bold' : ''
            }`}
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
              {isDelete && <X className="text-text-secondary ml-4 !w-4 !h-4" />}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
