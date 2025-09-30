'use client';
import { getMatchInfo, updateMatchInfo } from '@/api/matches';
import { MatchInfo } from '@/types/matchType';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';

export function useMatchInfo(matchId: number) {
  return useQuery<MatchInfo>({
    queryKey: ['matchInfo', matchId],
    queryFn: () => getMatchInfo(matchId),
    enabled: !!matchId,
  });
}

export function useUpdateMatchInfo(matchId: number) {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: { dailyQuestionHour: number; startDate?: string | null }) =>
      updateMatchInfo(matchId, data.dailyQuestionHour, data.startDate),
    onSuccess: (_data, payload) => {
      queryClient.setQueryData<MatchInfo>(['matchInfo', matchId], (old) =>
        old
          ? {
              ...old,
              dailyQuestionHour: payload.dailyQuestionHour,
              ...(payload.startDate != null ? { startDate: payload.startDate } : {}),
            }
          : old,
      );
    },
  });
}
