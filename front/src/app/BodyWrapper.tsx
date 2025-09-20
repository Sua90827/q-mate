'use client';
import { useThemeByTime } from '@/hooks/useThemeByTime';
import { usePathname } from 'next/navigation';

export default function BodyWrapper({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();
  const theme = useThemeByTime();

  let bgClass = 'bg-gradient-main';

  if (pathname.startsWith('/invite')) {
    bgClass = 'bg-gradient-main';
  } else if (pathname.startsWith('/login') || pathname.startsWith('/signup')) {
    bgClass = 'bg-bg-auth';
  } else if (pathname.startsWith('/main')) {
    if (theme === 'sunset') bgClass = 'bg-gradient-sunset';
    else if (theme === 'night') bgClass = 'bg-gradient-night';
    else bgClass = 'bg-gradient-main';
  } else {
    bgClass = 'bg-gradient-sub';
  }

  return <div className={`flex flex-col w-full min-h-screen ${bgClass} `}>{children}</div>;
}
