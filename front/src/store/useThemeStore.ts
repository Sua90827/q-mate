import { create } from 'zustand';
import { ThemeType } from '@/utils/getThemeByHour';

interface ThemeState {
  theme: ThemeType;
  setTheme: (theme: ThemeType) => void;
}

export const useThemeStore = create<ThemeState>((set) => ({
  theme: 'day',
  setTheme: (theme) => set({ theme }),
}));
