import { create } from 'zustand';
import { persist } from 'zustand/middleware';

interface AuthState {
  accessToken: string | null;
  setAccessToken: (accessToken: string) => void;
  resetAccessToken: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      setAccessToken: (accessToken) => set({ accessToken }),
      resetAccessToken: () => set({ accessToken: null }),
    }),
    {
      name: 'accessToken',
    },
  ),
);
