import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface SelectedState {
  matchId: string;
  setMatchId: (code: string) => void;
}

export const useMatchIdStore = create<SelectedState>()(
  persist(
    (set) => ({
      matchId: '',
      setMatchId: (code: string) => set({ matchId: code }),
    }),
    {
      name: 'matchCode',
    },
  ),
);
