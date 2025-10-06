import { fetchVapidPublicKey } from '@/api/notification';
import { create } from 'zustand';
import { persist } from 'zustand/middleware';

type State = {
  vapidPublicKey: string | null;
  setVapidPublicKey: (key: string) => void;
  ensureVapidPublicKey: () => Promise<string>;
};

export const useVapidPublicKeyStore = create<State>()(
  persist(
    (set, get) => ({
      vapidPublicKey: null,
      setVapidPublicKey: (key: string) => set({ vapidPublicKey: key }),
      ensureVapidPublicKey: async () => {
        const existing = get().vapidPublicKey;
        if (existing) return existing;

        const { vapidPublicKey } = await fetchVapidPublicKey();
        set({ vapidPublicKey });
        return vapidPublicKey;
      },
    }),
    {
      name: 'push-Key-storage',
    },
  ),
);
