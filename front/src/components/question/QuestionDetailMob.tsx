'use client';
import React, { useEffect, useState } from 'react';
import ShareBtn from './ui/ShareBtn';
import { useParams } from 'next/navigation';
import axios from 'axios';
import { X } from 'lucide-react';
import Link from 'next/link';

interface Answer {
  isMine: boolean;
  content: string;
}

interface QuestionDetail {
  questionInstanceId: number;
  question: { text: string };
  answers: Answer[];
}

export default function QuestionDetailMob() {
  const { id } = useParams<{ id: string }>();
  const [item, setItem] = useState<QuestionDetail | null>(null);

  useEffect(() => {
    const fetchData = async () => {
      const res = await axios.get<QuestionDetail[]>('http://localhost:3001/contents');
      const found = res.data.find((q) => q.questionInstanceId === Number(id));
      setItem(found ?? null);
    };
    fetchData();
  }, [id]);

  if (!item) return <div className="p-6">선택된 질문이 없습니다.</div>;

  return (
    <div className=" w-full h-screen bg-gradient-sub relative p-10 flex flex-col items-center md:hidden ">
      <Link href={'/question/list'}>
        <X className="absolute right-3 top-3" />
      </Link>

      <p className="text-text-secondary pt-10">#01</p>
      <h2 className="text-24 font-bold">{item.question.text}</h2>

      {item.answers.length === 2 && (
        <div className="mt-10 ">
          {/* 내 답변 */}
          <div className="pb-6">
            <p className="text-18">조용한 유령</p>
            <p className=" text-gray-500 text-16">{item.answers.find((a) => a.isMine)?.content}</p>
          </div>

          {/* 상대방 답변 */}
          <div>
            <p className="text-18">활기찬 고래</p>
            <p className="text-gray-500 text-16">{item.answers.find((a) => !a.isMine)?.content}</p>
          </div>
        </div>
      )}

      <ShareBtn />
    </div>
  );
}
