import { Theme } from '@/types/theme';
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface ThemeStore {
  theme: Theme;
  setTheme: (t: Theme) => void;
  hasHydrated: boolean;
}

export const useThemeStore = create<ThemeStore>()(
  persist(
    (set) => ({
      theme: 'day',
      setTheme: (t: Theme) => set({ theme: t }),
      hasHydrated: false,
    }),
    {
      name: 'theme',
      onRehydrateStorage: () => (state, error) => {
        if (error) return;
        // hydrate 완료 후에 hasHydrated true로 세팅
        setTimeout(() => {
          useThemeStore.setState({ hasHydrated: true });
        }, 0);
      },
    },
  ),
);
