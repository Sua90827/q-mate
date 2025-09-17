import React, { useEffect, useState } from 'react';
import SearchInput from './ui/SearchInput';
import { ListFilter, Trash2 } from 'lucide-react';
import { QuestionInstance } from './QuestionListWeb';
import axios from 'axios';
import Link from 'next/link';

export default function QuestionListMob() {
  const [query, setQuery] = useState('');
  const [data, setData] = useState<QuestionInstance[]>([]);
  const [active, setActive] = useState<number | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      const res = await axios.get<QuestionInstance[]>('http://localhost:3001/content');
      const questions = res.data;
      setData(questions);

      if (questions.length > 0) {
        setActive(questions[0].questionInstanceId);
      }
    };

    fetchData();
  }, []);

  const filtered = data.filter((list) =>
    list.question.text.toLowerCase().includes(query.toLowerCase()),
  );

  return (
    <div className="bg-gradient-sub w-full h-screen  py-5">
      <div className="flex justify-between items-center px-10">
        <ListFilter className="!w-[20px] !h-[20px]" />
        <p className="text-20 font-jalnan">질문 리스트</p>
        <Trash2 className="!w-[20px] !h-[20px]" />
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
            } ${list.status === 'PENDING' ? 'text-primary font-bold' : ''}`}
          >
            <Link
              href={`/question/list/detail/${list.questionInstanceId}`}
              onClick={() => setActive(list.questionInstanceId)}
            >
              {list.question.text.length > 17
                ? list.question.text.slice(0, 16) + '...'
                : list.question.text}
            </Link>
          </li>
        ))}
      </ul>
    </div>
  );
}
