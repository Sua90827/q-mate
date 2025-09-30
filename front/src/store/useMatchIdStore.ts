import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface SelectedState {
  matchId: number | null;
  setMatchId: (code: number) => void;
  resetMatchId: () => void;
}

export const useMatchIdStore = create<SelectedState>()(
  persist(
    (set) => ({
      matchId: null,
      setMatchId: (code: number) => set({ matchId: code }),
      resetMatchId: () => set({ matchId: null }),
    }),
    {
      name: 'matchCode',
    },
  ),
);
