'use client';

import {
  createSchedule,
  deleteSchedule,
  fetchEventDetail,
  fetchEventMonth,
  fetchScheduleList,
  updateSchedule,
} from '@/api/schedule';
import { EventMonthResponse, ScheduleEvent } from '@/types/scheduleType';
import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

//스케줄 리스트 조회
export const useScheduleList = (
  matchId: number,
  params: {
    from: string;
    to: string;
    page?: number;
    size?: number;
    repeatType?: 'NONE' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';
    anniversary?: boolean;
  },
) => {
  return useQuery({
    queryKey: ['schedule', matchId, params],
    queryFn: () => fetchScheduleList(matchId, params),
    staleTime: 0,
    refetchInterval: 1000 * 30,
    gcTime: 1000 * 60 * 60,
  });
};

//스케줄 등록
export const useCreateSchedule = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: createSchedule,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['schedule'] });
      queryClient.invalidateQueries({ queryKey: ['calendarMonth'] });
    },
  });
};

//스케줄 삭제
export const useDeleteSchedule = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ matchId, eventId }: { matchId: number; eventId: number }) =>
      deleteSchedule({ matchId, eventId }),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['schedule'] });
      queryClient.invalidateQueries({ queryKey: ['calendarMonth'] });
    },
  });
};

export const useEventMonth = (matchId: number, from: string, to: string) => {
  return useQuery<EventMonthResponse>({
    queryKey: ['calendarMonth', matchId, from, to],
    queryFn: () => fetchEventMonth(matchId, from, to),
    enabled: Boolean(matchId && from && to),
  });
};
export const useEventDetail = (matchId: number, eventId: number) => {
  return useQuery<ScheduleEvent>({
    queryKey: ['eventDetail', matchId, eventId],
    queryFn: () => fetchEventDetail(matchId, eventId),
    enabled: Number.isFinite(matchId) && Number.isFinite(eventId),
    staleTime: 1000 * 30,
    gcTime: 1000 * 60 * 10,
    refetchOnWindowFocus: false,
  });
};

// 스케줄 수정
export const useUpdateSchedule = (matchId: number, eventId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (body: {
      title: string;
      description: string | null;
      eventAt: string;
      repeatType: 'NONE' | 'WEEKLY' | 'MONTHLY' | 'YEARLY';
      alarmOption: 'NONE' | 'WEEK_BEFORE' | 'THREE_DAYS_BEFORE' | 'SAME_DAY';
    }) =>
      updateSchedule({
        matchId,
        eventId,
        title: body.title,
        description: body.description ?? undefined,
        eventAt: body.eventAt,
        repeatType: body.repeatType,
        alarmOption: body.alarmOption,
      }),

    onSuccess: (updated) => {
      queryClient.setQueryData(['eventDetail', matchId, eventId], updated);
      queryClient.invalidateQueries({ queryKey: ['schedule'] });
      queryClient.invalidateQueries({ queryKey: ['calendarMonth'] });
    },
  });
};
