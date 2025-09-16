import React, { useEffect, useState } from 'react';
import { QuestionInstance } from './QuestionListWeb';
import axios from 'axios';
import ShareBtn from './ui/ShareBtn';

interface Category {
  id: number;
  name: string;
}

interface Question {
  questionId: number;
  sourceType: 'ADMIN' | 'USER';
  relationType: 'COUPLE' | 'FRIEND';
  category: Category;
  text: string;
}

interface Answer {
  answerId: number;
  userId: number;
  isMine: boolean;
  visible: boolean;
  content: string;
  submittedAt: string;
}

export interface QuestionDetail {
  questionInstanceId: number;
  matchId: number;
  deliveredAt: string;
  status: 'PENDING' | 'COMPLETED';
  completedAt: string | null;
  question: Question;
  answers: Answer[];
}

export default function QuestionDetailWeb({ id, data }: { id: number; data: QuestionInstance[] }) {
  const item = data.find((q) => q.questionInstanceId === id);
  const [answers, setAnswers] = useState<Answer[]>([]);

  useEffect(() => {
    const fetchData = async () => {
      const res = await axios.get<QuestionDetail[]>('http://localhost:3001/contents');
      const found = res.data.find((q) => q.questionInstanceId === id);

      if (found && item && found.questionInstanceId === item.questionInstanceId) {
        setAnswers(found.answers);
      } else {
        setAnswers([]);
      }
    };
    fetchData();
  }, [id, item]);

  if (!item) return <div className="p-6">선택된 질문이 없습니다.</div>;

  return (
    <div className="relative p-10 flex flex-col items-center lg:w-[500px]  md:w-[450px] w-[350px] h-[500px] bg-secondary rounded-md shadow-md ">
      <p className="text-text-secondary">#01</p>
      <h2 className="text-24 font-bold">{item.question.text}</h2>

      {answers.length === 2 && (
        <div className="mt-10 ">
          {/* 내 답변 */}
          <div className="pb-6">
            <p className="text-20">조용한 유령</p>
            <p className=" text-gray-500 text-16">{answers.find((a) => a.isMine)?.content}</p>
          </div>

          {/* 상대방 답변 */}
          <div>
            <p className="text-20">활기찬 고래</p>
            <p className="text-gray-500 text-16">{answers.find((a) => !a.isMine)?.content}</p>
          </div>
        </div>
      )}

      <ShareBtn />
    </div>
  );
}
