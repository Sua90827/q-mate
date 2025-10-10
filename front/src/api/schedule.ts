import axios from 'axios';
import { EventMonthResponse, ScheduleEvent, ScheduleResponse } from '@/types/scheduleType';
import instance from './axiosInstance';

//일정리스트 조회
export const fetchScheduleList = async (
  matchId: number,
  params: {
    from: string;
    to: string;
    page?: number;
    size?: number;
    repeatType?: 'NONE' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';
    anniversary?: boolean;
  },
): Promise<ScheduleResponse> => {
  const res = await axios.get<ScheduleResponse>(`/api/matches/${matchId}/events`, { params });
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
//일정 수정
export const updateSchedule = async ({
  matchId,
  eventId,
  title,
  description,
  eventAt,
  repeatType,
  alarmOption,
}: {
  matchId: number;
  eventId: number;
  title: string;
  description?: string;
  eventAt: string;
  repeatType: 'NONE' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';
  alarmOption: 'NONE' | 'WEEK_BEFORE' | 'THREE_DAYS_BEFORE' | 'SAME_DAY';
}): Promise<ScheduleEvent> => {
  const res = await axios.patch<ScheduleEvent>(`/api/matches/${matchId}/events/${eventId}`, {
    title,
    description,
    eventAt,
    repeatType,
    alarmOption,
  });
  return res.data;
};
export const fetchEventMonth = async (
  matchId: number,
  from: string,
  to: string,
): Promise<EventMonthResponse> => {
  const res = await instance.get<EventMonthResponse>(`/api/matches/${matchId}/events/calendar`, {
    params: { from, to },
  });
  return res.data;
};

export const fetchEventDetail = async (matchId: number, eventId: number) => {
  const res = await instance.get(`/api/matches/${matchId}/events/${eventId}`);
  return res.data as ScheduleEvent;
};
