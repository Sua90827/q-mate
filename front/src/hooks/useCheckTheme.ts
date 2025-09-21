import { useThemeStore } from '@/store/useThemeStore';
import { Theme } from '@/types/theme';
import { useEffect, useState } from 'react';

// 시간에 따른 테마 분기
export function getThemeByHour(hours: number): Theme {
  if (hours >= 6 && hours < 18) return 'day';
  if (hours >= 18 && hours < 20) return 'sunset';
  return 'night';
}

export function useCheckTheme() {
  const { setTheme } = useThemeStore();
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true); //Hydration 방지
  }, []);

  useEffect(() => {
    if (!mounted) return;

    // 시간 체크
    const hours = new Date().getHours();
    setTheme(getThemeByHour(21));

    // 5분마다 시간 체크
    const timer = setInterval(() => {
      const h = new Date().getHours();
      setTheme(getThemeByHour(h));
    }, 5 * 60 * 1000);

    return () => clearInterval(timer);
  }, [mounted, setTheme]);
}
