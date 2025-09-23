'use client';

import { CalendarMinus2, House, MessageSquareText, Settings } from 'lucide-react';
import Link from 'next/link';
import React, { useState } from 'react';
import BellBtn from './BellBtn';

const NAV_ITEMS = [
  { key: 'home', label: '홈', href: '/main', Icon: House },
  { key: 'record', label: '우리의 기록', href: '/record', Icon: MessageSquareText },
  { key: 'schedule', label: '캘린더', href: '/schedule', Icon: CalendarMinus2 },
  { key: 'settings', label: '설정', href: '/settings', Icon: Settings },
];

export default function Nav() {
  const [active, setActive] = useState('home');

  return (
    <>
      {/* 모바일 (하단 고정) */}
      <nav className="sm:hidden fixed bottom-0 left-0 w-full h-[70px] bg-secondary flex justify-center items-center z-50">
        <ul className="w-[320px] gap-12 flex">
          {NAV_ITEMS.map(({ key, href, Icon, label }) => (
            <li key={key}>
              <Link href={href} onClick={() => setActive(key)}>
                <Icon
                  aria-label={label}
                  size={48}
                  className={`nav-item-mob ${active === key ? 'active' : ''}`}
                />
              </Link>
            </li>
          ))}
        </ul>
      </nav>

      {/* 데스크탑 (상단 고정) */}
      <header className="hidden sm:flex fixed top-0 left-0 w-full h-[70px] items-center z-40 bg-transparent">
        <Link href="/main">
          <img alt="큐메이트" width={109} height={35} className="mx-7 site-logo" />
        </Link>
        <nav className="w-full flex justify-end items-center">
          <ul className="gap-12 flex">
            {NAV_ITEMS.map(({ key, href, label }) => (
              <li key={key}>
                <Link
                  href={href}
                  onClick={() => setActive(key)}
                  className={`nav-item nav-item-web ${active === key ? 'active' : ''}`}
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
