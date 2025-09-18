'use client';
import { fetchScheduleList } from '@/api/schedule';
import { ScheduleResponse } from '@/types/scheduleType';
import { useQuery } from '@tanstack/react-query';

export const useScheduleList = () => {
  return useQuery<ScheduleResponse>({
    queryKey: ['schedule'],
    queryFn: fetchScheduleList,
    staleTime: 1000 * 60 * 10,
    gcTime: 1000 * 60 * 60,
  });
};
