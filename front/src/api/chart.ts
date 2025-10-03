import axios from 'axios';

//차트 통계 조회
export const fetchChart = async (matchId: number) => {
  const res = await axios.get(`/api/matches/${matchId}/stats/likes-by-category/monthly`);
  return res.data;
};
