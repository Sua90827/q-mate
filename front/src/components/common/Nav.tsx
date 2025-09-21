'use client';

import { CalendarMinus2, House, MessageSquareText, Settings } from 'lucide-react';
import Image from 'next/image';
import Link from 'next/link';
import React, { useState } from 'react';
import BellBtn from './BellBtn';
import { useThemeStore } from '@/store/useThemeStore';
import { Theme } from '@/types/theme';

const NAV_ITEMS = [
  { key: 'home', label: '홈', href: '/main', Icon: House },
  { key: 'record', label: '우리의 기록', href: '/record', Icon: MessageSquareText },
  { key: 'schedule', label: '캘린더', href: '/schedule', Icon: CalendarMinus2 },
  { key: 'settings', label: '설정', href: '/settings', Icon: Settings },
];

const activeClassWeb: Record<Theme, string> = {
  day: 'bg-primary',
  sunset: 'bg-sunset-active',
  night: 'bg-night-active',
};

const activeClassMob: Record<Theme, string> = {
  day: 'text-primary',
  sunset: 'text-sunset-active',
  night: 'text-night-active',
};

const changeLogo = {
  day: '/images/logo/day_logo.svg',
  sunset: '/images/logo/sunset_logo.svg',
  night: '/images/logo/night_logo.svg',
};

export default function Nav() {
  const [active, setActive] = useState('home');
  const theme = useThemeStore((state) => state.theme);

  return (
    <>
      {/* 모바일 (하단 고정) */}
      <nav className="sm:hidden fixed bottom-0 left-0 w-full h-[70px] bg-secondary flex justify-center items-center z-50">
        <ul className="w-[320px] gap-12 flex">
          {NAV_ITEMS.map(({ key, href, Icon, label }) => (
            <li key={key}>
              <Link href={href}>
                <Icon
                  aria-label={label}
                  size={48}
                  onClick={() => setActive(key)}
                  className={
                    active === key ? activeClassMob[theme] : 'hover:opacity-70 text-text-primary'
                  }
                />
              </Link>
            </li>
          ))}
        </ul>
      </nav>

      {/* 데스크탑 (상단 고정) */}
      <header className="hidden sm:flex fixed top-0 left-0 w-full h-[70px] items-center z-40 bg-transparent">
        <Link href="/main">
          <Image
            src={changeLogo[theme] || '/images/logo/day_logo.svg'}
            alt="큐메이트"
            width={109}
            height={35}
            className="mx-7"
          />
        </Link>
        <nav className="w-full flex justify-end items-center">
          <ul className={`gap-12 flex ${theme === 'night' ? 'text-secondary' : ''}`}>
            {NAV_ITEMS.map(({ key, href, label }) => (
              <li key={key}>
                <Link
                  href={href}
                  onClick={() => setActive(key)}
                  className={`font-bold text-16
                   ${
                     active === key
                       ? `rounded-2xl px-4 py-2 text-secondary ${activeClassWeb[theme]}`
                       : 'hover:opacity-70 px-4 py-2'
                   }`}
                >
                  {label}
                </Link>
              </li>
            ))}
            <li className="relative top-[-6px]">
              <BellBtn />
            </li>
          </ul>
        </nav>
      </header>
    </>
  );
}
