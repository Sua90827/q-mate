'use client';
import axios from 'axios';
import { useEffect, useState } from 'react';
import QuestionDetailWeb from './QuestionDetailWeb';
import SearchInput from './ui/SearchInput';
import Filter from './ui/Filter';

interface Question {
  questionId: number;
  text: string;
}

export interface QuestionInstance {
  questionInstanceId: number;
  deliveredAt: string;
  status: string;
  question: Question;
  completedAt: string;
}

export interface CustomQuestion {
  questionId: number;
  sourceType: string;
  relationType: string;
  category: Question;
  text: string;
  isActive: boolean;
  isEditable: boolean;
  createdAt: string;
}

export default function QuestionListWeb() {
  const [data, setData] = useState<QuestionInstance[]>([]);
  const [customData, setCustomData] = useState<CustomQuestion[]>([]);
  const [active, setActive] = useState<number | null>(null);
  const [query, setQuery] = useState('');

  useEffect(() => {
    const fetchData = async () => {
      const res = await axios.get<QuestionInstance[]>('http://localhost:3001/content');
      const questions = res.data;
      setData(questions);

      if (questions.length > 0) {
        setActive(questions[0].questionInstanceId);
      }
    };

    const fetchCustomData = async () => {
      const res = await axios.get<CustomQuestion[]>('http://localhost:3001/customs');
      const questions = res.data;
      setCustomData(questions);
    };

    fetchData();
    fetchCustomData();
  }, []);

  const normalizedCustomData = customData.map((c) => ({
    questionInstanceId: -c.questionId,
    deliveredAt: c.createdAt,
    status: 'CUSTOM',
    completedAt: '',
    question: { questionId: c.questionId, text: c.text },
  }));

  const totalData = [...data, ...normalizedCustomData];

  const filtered = totalData.filter((list) =>
    list.question.text.toLowerCase().includes(query.toLowerCase()),
  );
  return (
    <div className=" w-full h-screen bg-gradient-sub ">
      <div className="mx-5 md:mx-7 h-screen flex items-center">
        {/* 좌측 리스트 */}
        <div className="bg-secondary rounded-md shadow-md lg:w-[350px] w-[220px]  h-[550px]  flex flex-col ">
          <div className="mt-6">
            <SearchInput query={query} setQuery={setQuery} />
          </div>
          <Filter />
          <ul className="flex flex-col divide-y divide-gray border- flex-1 overflow-y-auto">
            {filtered.map((list) => (
              <li
                key={list.questionInstanceId}
                className={`py-5 pl-4 cursor-pointer ${
                  active === list.questionInstanceId ? 'bg-list-active font-bold' : ''
                } ${list.status === 'PENDING' ? 'text-primary font-bold' : ''} ${
                  list.status === 'CUSTOM' ? 'text-text-secondary bg-gray font-bold' : ''
                }`}
                onClick={() => setActive(list.questionInstanceId)}
              >
                {list.question.text.length > 17
                  ? list.question.text.slice(0, 16) + '...'
                  : list.question.text}
              </li>
            ))}
          </ul>
        </div>

        {/* 우측 상세 */}
        <div className="flex justify-center flex-1 ">
          {active !== null && <QuestionDetailWeb id={active} data={totalData} />}
        </div>
      </div>
    </div>
  );
}
