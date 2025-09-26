import axios from 'axios';
import { EventMonthResponse, ScheduleResponse } from '@/types/scheduleType';

export const fetchScheduleList = async (): Promise<ScheduleResponse> => {
  const res = await axios.get<ScheduleResponse>('http://localhost:3005/schedule');
  return res.data;
};

export const fetchEventMonth = async (year: number, month: number): Promise<EventMonthResponse> => {
  const res = await axios.get<EventMonthResponse>(`http://localhost:3006/calendarMonth`, {
    params: { year, month },
  });
  return res.data;
};
