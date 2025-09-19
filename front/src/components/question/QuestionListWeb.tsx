'use client';
import { useEffect, useMemo, useState } from 'react';
import QuestionDetailWeb from './QuestionDetailWeb';
import SearchInput from './ui/SearchInput';
import Filter from './ui/Filter';
import { X } from 'lucide-react';
import { useQuestions, useCustomQuestions } from '@/hooks/useQuestions';
import { QuestionInstance } from '@/types/questionType';

export default function QuestionListWeb() {
  const [query, setQuery] = useState('');
  const [showCustomOnly, setShowCustomOnly] = useState(false);
  const [active, setActive] = useState<number | null>(null);

  const { data: questions = [] } = useQuestions();
  const { data: customData = [] } = useCustomQuestions();

  // 커스텀 질문 일반 질문리스트와 포멧팅
  const normalizedCustomData: QuestionInstance[] = customData.map((c) => ({
    questionInstanceId: -c.questionId, // 충돌 방지용 음수 id
    deliveredAt: c.createdAt,
    status: 'EDITABLE',
    completedAt: '',
    question: { questionId: c.questionId, text: c.text },
  }));

  const totalData = useMemo(
    () => [...questions, ...normalizedCustomData],
    [questions, normalizedCustomData],
  );

  useEffect(() => {
    if (totalData.length > 0 && active === null) {
      setActive(totalData[0].questionInstanceId);
    }
  }, [totalData, active]);

  const filtered = totalData
    .filter((list) => list.question.text.toLowerCase().includes(query.toLowerCase()))
    .filter((list) => (showCustomOnly ? list.status === 'EDITABLE' : true));

  return (
    <div className="w-full h-screen bg-gradient-sub">
      <div className="mx-5 md:mx-7 h-screen flex items-center">
        {/* 좌측 리스트 */}
        <div className="bg-secondary rounded-md shadow-md lg:w-[350px] w-[220px] h-[550px] flex flex-col">
          <div className="mt-6">
            <SearchInput query={query} setQuery={setQuery} />
          </div>
          <div className="flex justify-between items-center">
            <span className="inline-block text-20 font-bold py-4 pl-4 cursor-none">
              질문 리스트
            </span>
            <Filter setShowCustomOnly={setShowCustomOnly} />
          </div>
          <ul className="flex flex-col divide-y divide-gray border- flex-1 overflow-y-auto">
            {filtered.map((list) => (
              <li
                key={list.questionInstanceId}
                className={`py-5 pl-4 cursor-pointer ${
                  active === list.questionInstanceId ? 'bg-list-active font-bold' : ''
                } ${list.status === 'PENDING' ? 'text-primary font-bold' : ''} ${
                  list.status === 'EDITABLE' ? 'text-text-secondary bg-gray font-bold' : ''
                }`}
                onClick={() => setActive(list.questionInstanceId)}
              >
                <div className="flex justify-between">
                  {list.question.text.length > 17
                    ? list.question.text.slice(0, 16) + '...'
                    : list.question.text}
                  {list.status === 'EDITABLE' && <X className="mx-4 !w-4 !h-4" />}
                </div>
              </li>
            ))}
          </ul>
        </div>

        {/* 우측 상세 */}
        <div className="flex justify-center flex-1">
          {active !== null && <QuestionDetailWeb id={active} data={totalData} />}
        </div>
      </div>
    </div>
  );
}
