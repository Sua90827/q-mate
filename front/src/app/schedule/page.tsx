import BellBtn from '@/components/common/BellBtn';
import ScheduleListWeb from '@/components/schedule/ScheduleListWeb';
import ScheduleView from '@/components/schedule/ScheduleView';
import { CalendarDays } from 'lucide-react';
import Link from 'next/link';
import React from 'react';
export default function page() {
  return (
    <div className="sm:pt-[35px] w-full h-full">
      <div className="fixed top-0 left-0 right-0 flex items-center justify-between py-5 sm:hidden ">
        <Link href="/schedule/list" aria-label="일정 리스트로 이동">
          <CalendarDays className="text-theme-primary ml-7" />
        </Link>

        <Link href="/main">
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

        <div className="flex-2 flex justify-center sm:pt-0 pt-[70px]">
          <ScheduleView />
        </div>
      </div>
    </div>
  );
}
