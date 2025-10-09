'use client';
import { CalendarDays } from 'lucide-react';
import Link from 'next/link';
import React from 'react';
import BellBtn from '../common/BellBtn';
import ScheduleListWeb from './ScheduleListWeb';
import ScheduleView from './ScheduleView';
import { useSelectedStore } from '@/store/useSelectedStore';

export default function Schedule() {
  const setSelectedMenu = useSelectedStore((state) => state.setSelectedMenu);
  return (
    <div className="sm:pt-[35px] w-full h-full">
      <div className="fixed top-0 left-0 right-0 flex items-center justify-between py-5 sm:hidden ">
        <Link href="/schedule/list" aria-label="일정 리스트로 이동">
          <CalendarDays className="text-theme-primary ml-7 !w-8 !h-8" />
        </Link>

        <Link href="/main" onClick={() => setSelectedMenu('home')}>
          <span
            className="site-logo inline-block w-[109px] h-[35px]"
            role="img"
            aria-label="큐메이트"
          />
        </Link>
        <BellBtn />
      </div>
      <div className="w-full h-full flex flex-row gap-10 justify-center md:justify-between md:pb-[70px] md:px-[20px] lg: xl:px-[120px]">
        <div className="flex-1 justify-center hidden md:flex flex-col md:min-w-[260px] lg:max-w-[380px] h-full">
          <ScheduleListWeb />
        </div>

        <div className="flex-2 flex justify-center pt-[70px] sm:pt-0">
          <ScheduleView />
        </div>
      </div>
    </div>
  );
}
