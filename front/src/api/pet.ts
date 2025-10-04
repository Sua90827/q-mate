import axios from 'axios';

export const fetchPetInfo = async (matchId: number) => {
  const res = await axios.get(`/api/matches/${matchId}/pet`);
  return res.data;
};
