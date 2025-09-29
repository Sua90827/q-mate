import {
  createCustomQuestion,
  deleteCustomQuestion,
  fetchCustomQuestions,
  updateCustomQuestion,
} from '@/api/custom';
import { useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

//조회
export const useFetchCustomQuestions = (matchId: number) => {
  return useQuery({
    queryKey: ['customQuestions'],
    queryFn: () => fetchCustomQuestions(matchId),
    staleTime: 0,
    gcTime: 1000 * 60 * 10,
  });
};

//등록
export const useCreateCustomQuestion = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ text, matchId }: { text: string; matchId: number }) =>
      createCustomQuestion({ text, matchId }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['customQuestions'] });
    },
  });
};

//수정
export const useUpdateCustomQuestion = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ text, id }: { text: string; id: number }) => updateCustomQuestion({ text, id }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['customQuestions'] });
    },
  });
};

//삭제
export const useDeleteCustomQuestion = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (id: number) => deleteCustomQuestion(id),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['customQuestions'] });
    },
  });
};
