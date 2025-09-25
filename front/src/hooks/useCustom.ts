import {
  createCustomQuestion,
  deleteCustomQuestion,
  fetchCustomQuestions,
  updateCustomQuestion,
} from '@/api/custom';
import { useMutation, useQuery } from '@tanstack/react-query';

//조회
export const useFetchCustomQuestions = () => {
  return useQuery({
    queryKey: ['customQuestions'],
    queryFn: fetchCustomQuestions,
    staleTime: 1000 * 60 * 5,
    gcTime: 1000 * 60 * 10,
  });
};

//등록
export const useCreateCustomQuestion = () => {
  return useMutation({
    mutationFn: ({ text, matchId }: { text: string; matchId: number }) =>
      createCustomQuestion({ text, matchId }),
  });
};

//수정
export const useUpdateCustomQuestion = () => {
  return useMutation({
    mutationFn: ({ text, id }: { text: string; id: number }) => updateCustomQuestion({ text, id }),
  });
};

//삭제
export const useDeleteCustomQuestion = () => {
  return useMutation({
    mutationFn: (id: number) => deleteCustomQuestion(id),
  });
};
