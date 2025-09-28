'use client';
import { fetchEventMonth, fetchScheduleList } from '@/api/schedule';
import { EventMonthResponse, ScheduleResponse } from '@/types/scheduleType';
import { keepPreviousData, useQuery } from '@tanstack/react-query';

export const useScheduleList = () => {
  return useQuery<ScheduleResponse>({
    queryKey: ['schedule'],
    queryFn: fetchScheduleList,
    staleTime: 1000 * 60 * 10,
    gcTime: 1000 * 60 * 60,
  });
};

export const useEventMonth = (year: number, month: number) => {
  return useQuery<EventMonthResponse>({
    queryKey: ['calendarMonth', year, month],
    queryFn: () => fetchEventMonth(year, month),

    placeholderData: keepPreviousData,
    staleTime: 1000 * 60 * 10,
    gcTime: 1000 * 60 * 60,
  });
};
