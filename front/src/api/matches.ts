import { MatchInfo } from '@/types/matchType';
import instance from './axiosInstance';

// 매칭 정보 조회
export const getMatchInfo = async (matchId: number): Promise<MatchInfo> => {
  const res = await instance.get(`/api/matches/${matchId}`);
  return res.data;
};

// 매칭 정보 업데이트(질문시간, 처음만난 날)
export const updateMatchInfo = async (
  matchId: number,
  dailyQuestionHour: number,
  startDate?: string | null,
) => {
  const body: Record<string, unknown> = { dailyQuestionHour };
  if (startDate !== null) {
    body.startDate = startDate;
  }

  const response = await instance.patch(`/api/matches/${matchId}/info`, body);
  return response.data as { message: string };
};

//매칭 연결 끊기
export const disconnectMatch = async (matchId: number) => {
  const res = await instance.post(`/api/matches/${matchId}/disconnect`);
  return res.data as { message: string };
};
//매칭 연결 복구
export const restoreMatch = async (matchId: number) => {
  const res = await instance.post(`/api/matches/${matchId}/restore`);
  return res.data as { message: string };
};

//매칭 정보 업데이트 (닉네임 변경)
export const updateNickname = async ({ nickname }: { nickname: string }) => {
  const res = await instance.patch('/api/users/me/nickname', { nickname });
  return res.data;
};
