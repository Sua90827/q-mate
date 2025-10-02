import axios from 'axios';
import { EventMonthResponse, ScheduleEvent, ScheduleResponse } from '@/types/scheduleType';

//일정리스트 조회
export const fetchScheduleList = async (matchId: number): Promise<ScheduleResponse> => {
  const res = await axios.get<ScheduleResponse>(`/api/matches/${matchId}/events`);
  return res.data;
};

//일정 등록
export const createSchedule = async ({
  matchId,
  title,
  description,
  eventAt,
  repeatType,
  alarmOption,
}: {
  matchId: number;
  title: string;
  description: string;
  eventAt: string;
  repeatType: 'WEEKLY' | 'MONTHLY' | 'YEARLY' | 'NONE';
  alarmOption: 'NONE' | 'WEEK_BEFORE' | 'THREE_DAYS_BEFORE' | 'SAME_DAY';
}) => {
  const res = await axios.post(`/api/matches/${matchId}/events`, {
    title,
    description,
    eventAt,
    repeatType,
    alarmOption,
  });
  return res.data;
};

//일정 삭제
export const deleteSchedule = async ({
  matchId,
  eventId,
}: {
  matchId: number;
  eventId: number;
}) => {
  const res = await axios.delete(`/api/matches/${matchId}/events/${eventId}`);
  return res.data;
};

export const fetchEventMonth = async (year: number, month: number): Promise<EventMonthResponse> => {
  const res = await axios.get<EventMonthResponse>(`http://localhost:3006/calendarMonth`, {
    params: { year, month },
  });
  return res.data;
};

export const fetchEventDetail = async (matchId: number, eventId: number) => {
  const res = await axios.get(`/api/matches/${matchId}/events/${eventId}`);
  return res.data as ScheduleEvent;
};
