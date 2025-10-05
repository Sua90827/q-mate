import { create } from 'zustand';

interface PetState {
  currentExp: number;
  setCurrentExp: (exp: number) => void;
  bubbleTrigger: boolean;
  triggerBubble: () => void;
  resetBubble: () => void;
  reset: () => void;
}

export const usePetStateStore = create<PetState>((set) => ({
  currentExp: 0,
  setCurrentExp: (exp) => set({ currentExp: exp }),
  bubbleTrigger: false,
  triggerBubble: () => set({ bubbleTrigger: true }),
  resetBubble: () => set({ bubbleTrigger: false }),
  reset: () => set({ bubbleTrigger: false }),
}));
