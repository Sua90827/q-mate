import axios from 'axios';
import { ScheduleResponse } from '@/types/scheduleType';

export const fetchScheduleList = async (): Promise<ScheduleResponse> => {
  const res = await axios.get<ScheduleResponse>('http://localhost:3001/schedule');
  return res.data;
};
