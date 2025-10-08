import { useQuery } from '@tanstack/react-query';
import {
  fetchQuestions,
  fetchQuestionDetail,
  fetchTodayQuestion,
  answerQuestion,
  updateAnswer,
  ratingQuestion,
} from '../api/questions';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { QuestionResponse } from '@/types/questionType';

//전체 질문 조회
export const useQuestions = (matchId: number, page: number = 0, size: number = 20) => {
  return useQuery<QuestionResponse>({
    queryKey: ['questions', matchId, page, size],
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
    staleTime: 1000 * 60,
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
//답변
export const useAnswerQuestion = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({
      questionInstanceId,
      content,
    }: {
      questionInstanceId: number;
      content: string;
    }) => answerQuestion(questionInstanceId, content),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['todayQuestion'] });
      queryClient.invalidateQueries({ queryKey: ['questionDetail', variables.questionInstanceId] });
    },
  });
};
//답변 수정
export const useUpdateAnswerQuestion = () => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: ({ answerId, content }: { answerId: number; content: string }) =>
      updateAnswer(answerId, content),
    onSuccess: (_data, variables) => {
      queryClient.invalidateQueries({ queryKey: ['todayQuestion'] });
      queryClient.invalidateQueries({ queryKey: ['questionDetail', variables.answerId] });
    },
  });
};

//질문 평가
export const useRateQuestion = () => {
  return useMutation({
    mutationFn: ({ questionId, isLike }: { questionId: number; isLike: boolean }) =>
      ratingQuestion(questionId, isLike),
  });
};
