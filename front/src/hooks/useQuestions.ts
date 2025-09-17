import { useQuery } from '@tanstack/react-query';
import { fetchQuestions, fetchCustomQuestions, fetchQuestionDetail } from '../api/questions';

export const useQuestions = () => {
  return useQuery({
    queryKey: ['questions'],
    queryFn: fetchQuestions,
    staleTime: 1000 * 60 * 60 * 24,
    gcTime: 1000 * 60 * 60 * 24,
  });
};

export const useCustomQuestions = () => {
  return useQuery({
    queryKey: ['customQuestions'],
    queryFn: fetchCustomQuestions,
    staleTime: 1000 * 60 * 5,
    gcTime: 1000 * 60 * 10,
  });
};

export const useQuestionDetail = (id: number) => {
  return useQuery({
    queryKey: ['questionDetail', id],
    queryFn: () => fetchQuestionDetail(id),
    staleTime: 1000 * 30,
    gcTime: 1000 * 60 * 5,
    enabled: !!id,
  });
};
