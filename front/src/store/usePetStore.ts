import { create } from 'zustand';

interface PetState {
  currentExp: number;
  setCurrentExp: (exp: number) => void;
  addExp: (amount: number) => void;
  bubbleTrigger: boolean;
  triggerBubble: () => void;
  resetBubble: () => void;
  reset: () => void;
}

export const usePetStateStore = create<PetState>((set) => ({
  currentExp: 0,
  setCurrentExp: (exp) => set({ currentExp: exp }),
  addExp: (amount) => set((state) => ({ currentExp: state.currentExp + amount })),
  bubbleTrigger: false,
  triggerBubble: () => set({ bubbleTrigger: true }),
  resetBubble: () => set({ bubbleTrigger: false }),
  reset: () => set({ bubbleTrigger: false }),
}));
