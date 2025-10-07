import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface SelectedState {
  matchId: number | null;
  setMatchId: (matchId: number) => void;
  resetMatchId: () => void;
}

export const useMatchIdStore = create<SelectedState>()(
  persist(
    (set) => ({
      matchId: null,
      setMatchId: (id) => set({ matchId: id }),
      resetMatchId: () => set({ matchId: null }),
    }),
    {
      name: 'matchCode',
    },
  ),
);
