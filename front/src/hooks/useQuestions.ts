import { useQuery } from '@tanstack/react-query';
import { fetchQuestions, fetchQuestionDetail, fetchTodayQuestion } from '../api/questions';

//전체 질문 조회
export const useQuestions = () => {
  return useQuery({
    queryKey: ['questions'],
    queryFn: fetchQuestions,
    staleTime: 1000 * 60 * 60 * 24,
    gcTime: 1000 * 60 * 60 * 24,
  });
};

//질문 상세 조회
export const useQuestionDetail = (id: number) => {
  return useQuery({
    queryKey: ['questionDetail', id],
    queryFn: () => fetchQuestionDetail(id),
    staleTime: 1000 * 30,
    gcTime: 1000 * 60 * 5,
    enabled: !!id,
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
