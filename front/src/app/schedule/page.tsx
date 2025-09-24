import BellBtn from '@/components/common/BellBtn';
import ScheduleListWeb from '@/components/schedule/ScheduleListWeb';
import ScheduleView from '@/components/schedule/ScheduleView';
import AddBtn from '@/components/schedule/ui/AddBtn';
import { CalendarDays } from 'lucide-react';
import Image from 'next/image';
import Link from 'next/link';
import React from 'react';

export default function page() {
  return (
    <>
      <div className="flex w-full h-[70px] items-center justify-between sm:hidden">
        <Link href="/schedule/list" aria-label="일정 리스트로 이동">
          <CalendarDays className="text-theme-primary ml-7" />
        </Link>

        <Image
          src="/images/logo/logo.svg"
          alt="큐메이트"
          width={109}
          height={35}
          className="site-logo"
        />
        <BellBtn />
      </div>
      <div className="w-full h-full flex flex-row gap-30 justify-center">
        <ScheduleListWeb />
        <ScheduleView />
      </div>
    </>
  );
}
