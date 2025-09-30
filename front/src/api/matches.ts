import { MatchInfo } from '@/types/matchType';
import axios from 'axios';

// 매칭 정보 조회
export const getMatchInfo = async (matchId: number): Promise<MatchInfo> => {
  const res = await axios.get(`/api/matches/${matchId}`);
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

  const response = await axios.patch(`/api/matches/${matchId}/info`, body);
  return response.data as { message: string };
};
