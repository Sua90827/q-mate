import axios from 'axios';
import instance from './axiosInstance';

export const fetchPetInfo = async (matchId: number) => {
  const res = await instance.get(`/api/matches/${matchId}/pet`);
  return res.data;
};
