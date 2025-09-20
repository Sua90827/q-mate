'use client';
import { usePathname } from 'next/navigation';

export default function BodyWrapper({ children }: { children: React.ReactNode }) {
  const pathname = usePathname();

  const bgClass =
    pathname.startsWith('/invite') || pathname.startsWith('/main')
      ? 'bg-gradient-main'
      : pathname.startsWith('/login') || pathname.startsWith('/signup')
      ? 'bg-bg-auth'
      : 'bg-gradient-sub';

  return <div className={`flex flex-col w-full min-h-screen ${bgClass}`}>{children}</div>;
}
