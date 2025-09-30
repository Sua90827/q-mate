import { connectWithInviteCode, createInviteCode } from '@/api/invite';
import { useMutation } from '@tanstack/react-query';

//초대 코드 발급
export const useCreateInviteCode = () => {
  return useMutation({
    mutationFn: ({
      relationType,
      startDate,
    }: {
      relationType: 'COUPLE' | 'FRIEND';
      startDate: string | null;
    }) => createInviteCode({ relationType, startDate }),
  });
};

//초대 코드 연결
export const useCreateMatchId = () => {
  return useMutation({
    mutationFn: ({ inviteCode }: { inviteCode: string }) => connectWithInviteCode({ inviteCode }),
  });
};
