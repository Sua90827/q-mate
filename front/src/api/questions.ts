import {
  CustomQuestion,
  QuestionDetail,
  QuestionInstance,
  TodayQuestion,
} from '@/types/questionType';
import axios from 'axios';

export const fetchQuestions = async (): Promise<QuestionInstance[]> => {
  const res = await axios.get('http://localhost:3001/content');
  return res.data;
};

export const fetchQuestionDetail = async (id: number): Promise<QuestionDetail | null> => {
  const res = await axios.get<QuestionDetail[]>('http://localhost:3001/contents');
  return res.data.find((q) => q.questionInstanceId === id) ?? null;
};

export const fetchCustomQuestions = async (): Promise<CustomQuestion[]> => {
  const res = await axios.get('http://localhost:3001/customs');
  return res.data;
};

// 오늘의 질문 가져오기
export const fetchTodayQuestion = async (): Promise<TodayQuestion> => {
  const res = await axios.get('http://localhost:4000/todayQuestion');
  return res.data;
};
