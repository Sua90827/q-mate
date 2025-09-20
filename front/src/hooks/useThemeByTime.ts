import { useMemo } from 'react';

type Theme = 'day' | 'sunset' | 'night'; // 낮, 노을, 밤

export function useThemeByTime(): Theme {
  const hours = new Date().getHours();

  return useMemo(() => {
    if (hours >= 6 && hours < 18) return 'day';
    if (hours >= 18 && hours < 20) return 'sunset';
    return 'night';
  }, [hours]);
}
