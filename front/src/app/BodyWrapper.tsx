'use client';

import { useThemeStore } from '@/store/useThemeStore';
import { usePathname } from 'next/navigation';
import { useCheckTheme } from '@/hooks/useCheckTheme';

export default function BodyWrapper({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const { theme, hasHydrated } = useThemeStore();

  useCheckTheme();

  let bgClass = 'bg-gradient-main';

  if (pathname.startsWith('/login') || pathname.startsWith('/signup')) {
    bgClass = 'bg-bg-auth';
  } else if (pathname.startsWith('/invite')) {
    bgClass = 'bg-gradient-main';
  } else if (hasHydrated) {
    if (theme === 'sunset') bgClass = 'bg-gradient-sunset';
    else if (theme === 'night') bgClass = 'bg-gradient-night';
  }

  return <div className={`flex flex-col h-full w-full ${bgClass}`}>{children}</div>;
}
