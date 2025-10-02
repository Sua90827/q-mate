'use client';
import {
  createSchedule,
  deleteSchedule,
  fetchEventDetail,
  fetchEventMonth,
  fetchScheduleList,
} from '@/api/schedule';
import { EventMonthResponse, ScheduleEvent, ScheduleResponse } from '@/types/scheduleType';
import { keepPreviousData, useMutation, useQuery, useQueryClient } from '@tanstack/react-query';

//스케줄 리스트 조회
export const useScheduleList = (matchId: number) => {
  return useQuery<ScheduleResponse>({
    queryKey: ['schedule'],
    queryFn: () => fetchScheduleList(matchId),
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
    },
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
export const useEventDetail = (matchId: number, eventId: number) => {
  return useQuery<ScheduleEvent>({
    queryKey: ['eventDetail', matchId, eventId],
    queryFn: () => fetchEventDetail(matchId, eventId),
    enabled: Number.isFinite(matchId) && Number.isFinite(eventId),
    staleTime: 1000 * 30,
    gcTime: 1000 * 60 * 10,
    refetchOnWindowFocus: false,
    placeholderData: keepPreviousData,
  });
};
