'use client';

import { CalendarMinus2, House, MessageSquareText, Settings } from 'lucide-react';
import Link from 'next/link';
import React from 'react';
import BellBtn from './BellBtn';
import { useSelectedStore } from '@/store/useSelectedStore';

const NAV_ITEMS = [
  { key: 'home', label: '홈', href: '/main', Icon: House },
  { key: 'record', label: '우리의 기록', href: '/record', Icon: MessageSquareText },
  { key: 'schedule', label: '캘린더', href: '/schedule', Icon: CalendarMinus2 },
  { key: 'settings', label: '설정', href: '/settings', Icon: Settings },
];

export default function Nav() {
  const selectedMenu = useSelectedStore((state) => state.selectedMenu);
  const setSelectedMenu = useSelectedStore((state) => state.setSelectedMenu);

  return (
    <>
      {/* 모바일 (하단 고정) */}
      <nav className="sm:hidden fixed bottom-0 left-0 w-full h-[70px] bg-secondary flex justify-center items-center z-50 border-t-gray border-1 shadow-[0_-2px_6px_rgba(0,0,0,0.1)]">
        <ul className="w-[320px] gap-10 flex">
          {NAV_ITEMS.map(({ key, href, Icon, label }) => (
            <li key={key}>
              <Link
                href={href}
                onClick={() => {
                  setSelectedMenu(key);
                }}
              >
                <Icon
                  aria-label={label}
                  size={48}
                  className={`nav-item-mob ${selectedMenu === key ? 'active' : ''}`}
                />
              </Link>
            </li>
          ))}
        </ul>
      </nav>

      {/* 데스크탑 (상단 고정) */}
      <header className="hidden sm:flex fixed top-0 left-0 w-full h-[70px] items-center z-40 bg-transparent">
        <Link href="/main" onClick={() => setSelectedMenu('home')}>
          <img
            src="/logo.svg"
            alt="큐메이트 로고"
            className="site-logo inline-block w-[109px] h-[35px] ml-7"
            aria-label="큐메이트"
          />
        </Link>
        <nav className="w-full flex justify-end items-center">
          <ul className="gap-10 flex items-center">
            {NAV_ITEMS.map(({ key, href, label }) => (
              <li key={key}>
                <Link
                  href={href}
                  onClick={() => {
                    setSelectedMenu(key);
                  }}
                  className={`nav-item nav-item-web ${selectedMenu === key ? 'active' : ''}`}
                >
                  {label}
                </Link>
              </li>
            ))}
            <li className="relative">
              <BellBtn />
            </li>
          </ul>
        </nav>
      </header>
    </>
  );
}
