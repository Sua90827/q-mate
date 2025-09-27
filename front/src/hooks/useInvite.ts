import { connectWithInviteCode, createInviteCode } from '@/api/invite';
import { useMutation } from '@tanstack/react-query';

//초대 코드 반환 API
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

//매칭 연결 모달에 사용
export const useCreateMatchId = () => {
  return useMutation({
    mutationFn: ({ inviteCode }: { inviteCode: string }) => connectWithInviteCode({ inviteCode }),
  });
};
