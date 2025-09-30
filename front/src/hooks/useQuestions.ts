import { useQuery } from '@tanstack/react-query';
import { fetchQuestions, fetchQuestionDetail, fetchTodayQuestion } from '../api/questions';
import { QuestionResponse } from '@/types/questionType';

//전체 질문 조회

export const useQuestions = (matchId: number, page: number = 0, size: number = 20) => {
  return useQuery<QuestionResponse>({
    queryKey: ['questions', matchId, page, size], // 의존성에 page, size 포함
    queryFn: () => fetchQuestions(matchId, page, size),
    staleTime: 1000 * 60 * 60 * 24,
    gcTime: 1000 * 60 * 60 * 24,
  });
};

//질문 상세 조회
export const useQuestionDetail = (questionInstanceId: number) => {
  return useQuery({
    queryKey: ['questionDetail', questionInstanceId],
    queryFn: () => fetchQuestionDetail(questionInstanceId),
    staleTime: 1000 * 30,
    gcTime: 1000 * 60 * 5,
    enabled: !!questionInstanceId,
  });
};

//오늘의 질문 hook
export function useTodayQuestion(matchId: number) {
  return useQuery({
    queryKey: ['todayQuestion', matchId],
    queryFn: () => fetchTodayQuestion(matchId),
    staleTime: 1000 * 60 * 5,
    gcTime: 1000 * 60 * 10,
    enabled: !!matchId,
  });
}
