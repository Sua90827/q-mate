import axios from 'axios';

//초대 코드 발급 (응답 : inviteCode, matchId)
export const createInviteCode = async ({
  relationType,
  startDate,
}: {
  relationType: 'COUPLE' | 'FRIEND';
  startDate: string | null;
}) => {
  const res = await axios.post(`/api/matches`, { relationType, startDate });
  return res.data;
};

//초대 코드 입력(연결) (응답 : matchId, message, partnerNickname)
export const connectWithInviteCode = async ({ inviteCode }: { inviteCode: string }) => {
  const res = await axios.post(`/api/matches/join`, { inviteCode });
  return res.data;
};
