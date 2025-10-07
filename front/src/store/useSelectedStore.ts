import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface SelectedState {
  selectedMenu: string;
  setSelectedMenu: (selectedMenu: string) => void;
  resetSelectedMenu: () => void;
}

export const useSelectedStore = create<SelectedState>()(
  persist(
    (set) => ({
      selectedMenu: '기본값',
      setSelectedMenu: (selectedMenu) => set({ selectedMenu }),
      resetSelectedMenu: () => set({ selectedMenu: '기본값' }),
    }),
    {
      name: 'selected',
    },
  ),
);
