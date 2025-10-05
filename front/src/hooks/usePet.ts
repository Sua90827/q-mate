import { fetchPetInfo } from '@/api/pet';
import { useQuery } from '@tanstack/react-query';

export const useFetchPetInfo = (matchId: number) => {
  return useQuery({
    queryKey: ['pet'],
    queryFn: () => fetchPetInfo(matchId),
    staleTime: 1000 * 60 * 60 * 24,
    gcTime: 1000 * 60 * 10,
  });
};
