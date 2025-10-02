import { fetchChart } from '@/api/chart';
import { Chart } from '@/types/chart';
import { useQuery } from '@tanstack/react-query';

export const useFetchChart = (matchId: number) => {
  return useQuery<Chart>({
    queryKey: ['chart', matchId],
    queryFn: () => fetchChart(matchId),
  });
};
