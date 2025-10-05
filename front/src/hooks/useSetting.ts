import { updateNickname } from '@/api/matches';
import { useMutation, useQueryClient } from '@tanstack/react-query';

//닉네임 변경
export const useUpdateNickname = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ nickname }: { nickname: string }) => updateNickname({ nickname }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['nickname'] });
    },
  });
};
