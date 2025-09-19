import {
  CustomQuestion,
  QuestionDetail,
  QuestionInstance,
  TodayQuestion,
} from '@/types/questionType';
import axios from 'axios';

// 질문 리스트 가져오기
export const fetchQuestions = async (): Promise<QuestionInstance[]> => {
  const res = await axios.get('http://localhost:3003/content');
  return res.data;
};

// 답변 상세 가져오기
export const fetchQuestionDetail = async (id: number): Promise<QuestionDetail | null> => {
  const res = await axios.get<QuestionDetail[]>('http://localhost:3004/contents');
  return res.data.find((q) => q.questionInstanceId === id) ?? null;
};

// 커스텀 질문 가져오기
export const fetchCustomQuestions = async (): Promise<CustomQuestion[]> => {
  const res = await axios.get('http://localhost:3005/customs');
  return res.data;
};

// 오늘의 질문 가져오기
export const fetchTodayQuestion = async (): Promise<TodayQuestion> => {
  const res = await axios.get('http://localhost:4000/todayQuestion');
  return res.data;
};
