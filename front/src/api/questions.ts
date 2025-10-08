import { AnswerResponseItem, QuestionResponse, TodayQuestion } from '@/types/questionType';
import axios from 'axios';

// 전체 질문 리스트 가져오기
export const fetchQuestions = async (
  matchId: number,
  page: number = 0,
  size: number = 20,
): Promise<QuestionResponse> => {
  const res = await axios.get(`/api/matches/${matchId}/question-instances`, {
    params: {
      page,
      size,
      status: ['PENDING', 'COMPLETED'],
    },
  });
  return res.data;
};

// 답변 상세 가져오기
export const fetchQuestionDetail = async (
  questionInstanceId: number,
): Promise<AnswerResponseItem | null> => {
  const res = await axios.get<AnswerResponseItem>(`/api/question-instances/${questionInstanceId}`);
  return res.data;
};

// 오늘의 질문 가져오기
export const fetchTodayQuestion = async (matchId: number): Promise<TodayQuestion> => {
  const res = await axios.get(`/api/matches/${matchId}/questions/today`);
  return res.data;
};

// 답변 하기
export const answerQuestion = async (questionInstanceId: number, content: string) => {
  const res = await axios.post(`/api/question-instances/${questionInstanceId}/answers`, {
    content,
  });
  return res.data;
};
// 답변 수정하기
export const updateAnswer = async (answerId: number, content: string) => {
  const res = await axios.patch(`/api/answers/${answerId}`, { content });
  return res.data;
};

//질문 평가
export const ratingQuestion = async (questionId: number, isLike: boolean) => {
  const res = await axios.post(`/api/questions/${questionId}/ratings`, { isLike });
  return res.data;
};
