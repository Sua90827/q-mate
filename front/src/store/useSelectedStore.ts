import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface SelectedState {
  selectedMenu: string;
  setSelectedMenu: (item: string) => void;
}

export const useSelectedStore = create<SelectedState>()(
  persist(
    (set) => ({
      selectedMenu: '기본값',
      setSelectedMenu: (item: string) => set({ selectedMenu: item }),
    }),
    {
      name: 'selected',
    },
  ),
);
