import {
  checkInviteCode,
  connectWithInviteCode,
  createInviteCode,
  fetchLockStatus,
} from '@/api/invite';
import { useMutation, useQuery } from '@tanstack/react-query';

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

// 계정조회
export const useFetchLockStatus = () => {
  return useQuery({
    queryKey: ['lockStatus'],
    queryFn: fetchLockStatus,
    staleTime: 0,
    gcTime: 1000 * 60 * 10,
  });
};

//초대코드 유효성 검증
export const useCheckInviteCode = () => {
  return useMutation({
    mutationFn: ({ inviteCode }: { inviteCode: string }) => checkInviteCode({ inviteCode }),
  });
};
