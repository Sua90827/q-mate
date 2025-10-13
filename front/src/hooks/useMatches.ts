'use client';
import { disconnectMatch, getMatchInfo, restoreMatch, updateMatchInfo } from '@/api/matches';
import { MatchInfo } from '@/types/matchType';
import { useQuery, useMutation, useQueryClient, UseQueryOptions } from '@tanstack/react-query';

export function useMatchInfo(
  matchId: number,
  options?: Omit<UseQueryOptions<MatchInfo>, 'queryKey' | 'queryFn'>,
) {
  return useQuery<MatchInfo>({
    queryKey: ['matchInfo', matchId],
    queryFn: () => getMatchInfo(matchId),
    enabled: !!matchId,
    ...options, // 페이지별로 옵션 덮어쓰기 가능
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
//매칭 연결 끊기
export function useDisconnectMatch(matchId: number) {
  return useMutation({
    mutationFn: () => disconnectMatch(matchId),
  });
}
//매칭 연결 복구
export function useRestoreMatch(matchId: number) {
  return useMutation({
    mutationFn: () => restoreMatch(matchId),
  });
}
