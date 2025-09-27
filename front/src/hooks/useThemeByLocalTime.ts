'use client';

import { useEffect } from 'react';
import { getThemeByHour } from '@/utils/getThemeByHour';
import { useThemeStore } from '@/store/useThemeStore';

export function useLocalTimeTheme() {
  const { setTheme } = useThemeStore();

  useEffect(() => {
    const hour = new Date().getHours();
    setTheme(getThemeByHour(hour));

    const interval = setInterval(() => {
      const newHour = new Date().getHours();
      setTheme(getThemeByHour(newHour));
    }, 60 * 1000);

    return () => clearInterval(interval);
  }, [setTheme]);
}
