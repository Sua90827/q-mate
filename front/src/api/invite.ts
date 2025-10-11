import { LockStatus } from '@/types/InviteType';
import { instance } from '../lib/axiosInstance';

//초대 코드 발급
export const createInviteCode = async ({
  relationType,
  startDate,
}: {
  relationType: 'COUPLE' | 'FRIEND';
  startDate: string | null;
}) => {
  const res = await instance.post(`/matches`, { relationType, startDate });
  return res.data;
};

//초대 코드 연결
export const connectWithInviteCode = async ({ inviteCode }: { inviteCode: string }) => {
  const res = await instance.post(`/matches/join`, { inviteCode });
  return res.data;
};

// 계정 잠김 조회
export const fetchLockStatus = async (): Promise<LockStatus> => {
  const res = await instance.get(`/matches/lock-status`);
  return res.data;
};

//초대코드 유효성 검증
export const checkInviteCode = async ({ inviteCode }: { inviteCode: string }) => {
  const res = await instance.post(`/invites/validate`, { inviteCode });
  return res.data;
};
