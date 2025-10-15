import instance from '../lib/axiosInstance';

// //차트 통계 조회
// export const fetchChart = async (matchId: number) => {
//   const res = await instance.get(`/api/matches/${matchId}/stats/likes-by-category/monthly`);
//   return res.data;
// };

export const fetchChart = async (matchId: number) => {
  const res = await instance.get(`/api/matches/${matchId}/stats/likes-by-category/monthly`, {
    params: { anchorDate: '2025-11-01' },
  });
  return res.data;
};
