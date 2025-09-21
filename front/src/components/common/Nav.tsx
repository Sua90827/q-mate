'use client';
import { CalendarMinus2, House, MessageSquareText, Settings } from 'lucide-react';
import Image from 'next/image';
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
      {/* 모바일 */}
      <nav className="w-full h-[70px] bg-secondary flex justify-center items-center sm:hidden">
        <ul className="w-[320px] gap-12 flex sm:hidden">
          {NAV_ITEMS.map(({ key, href, Icon, label }) => (
            <li key={key}>
              <Link href={href}>
                <Icon
                  aria-label={label}
                  size={48}
                  onClick={() => setActive(key)}
                  className={active === key ? 'text-primary' : 'hover:opacity-70'}
                />
              </Link>
            </li>
          ))}
        </ul>
      </nav>

      {/* 데스크탑 */}
      <header className="hidden sm:flex h-[70px] items-center w-full ">
        <Link href="/main">
          <Image src="/logo.svg" alt="큐메이트" width={109} height={35} className="mx-7" />
        </Link>
        <nav className="w-full h-[70px] sm:flex sm:justify-end items-center hidden sticky top-0">
          <ul className="gap-14 hidden sm:flex">
            {NAV_ITEMS.map(({ key, href, label }) => (
              <li key={key}>
                <Link
                  href={href}
                  onClick={() => setActive(key)}
                  className={
                    active === key
                      ? 'font-bold text-lg bg-primary rounded-xl py-2 px-3 text-secondary'
                      : 'font-bold text-lg hover:opacity-70'
                  }
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
