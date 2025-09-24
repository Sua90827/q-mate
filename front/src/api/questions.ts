import {
  AnswerResponseItem,
  CustomQuestion,
  QuestionResponse,
  TodayQuestion,
} from '@/types/questionType';
import axios from 'axios';

// 질문 리스트 가져오기
export const fetchQuestions = async (): Promise<QuestionResponse[]> => {
  const res = await axios.get('http://localhost:3003/question');
  return res.data;
};

// 답변 상세 가져오기
export const fetchQuestionDetail = async (id: number): Promise<AnswerResponseItem | null> => {
  const res = await axios.get<AnswerResponseItem[]>('http://localhost:3003/answer');
  return res.data.find((q) => q.questionInstanceId === id) ?? null;
};

// 커스텀 질문 가져오기
export const fetchCustomQuestions = async (): Promise<CustomQuestion[]> => {
  const res = await axios.get('http://localhost:3003/customs');
  return res.data;
};

// 오늘의 질문 가져오기
export const fetchTodayQuestion = async (): Promise<TodayQuestion> => {
  const res = await axios.get('http://localhost:4000/todayQuestion');
  return res.data;
};
