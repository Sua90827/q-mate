import { create } from 'zustand';
import { persist, createJSONStorage } from 'zustand/middleware';

const STORAGE_KEY = 'accessToken';
const safeRemovePersist = () => {
  if (typeof window === 'undefined') return;
  try {
    localStorage.removeItem(STORAGE_KEY);
    sessionStorage.removeItem(STORAGE_KEY);
  } catch {}
};

interface AuthState {
  accessToken: string | null;
  setAccessToken: (accessToken: string | null) => void;
  resetAccessToken: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      accessToken: null,
      setAccessToken: (accessToken) => {
        set({ accessToken });
        if (accessToken === null) {
          safeRemovePersist();
        }
      },
      resetAccessToken: () => {
        set({ accessToken: null });
        safeRemovePersist();
      },
    }),
    {
      name: STORAGE_KEY,
      storage: createJSONStorage(() =>
        typeof window !== 'undefined' ? localStorage : (undefined as unknown as Storage),
      ),
      partialize: (state) => ({ accessToken: state.accessToken }),
    },
  ),
);
