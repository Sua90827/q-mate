import ScheduleListWeb from '@/components/schedule/ScheduleListWeb';
import AddBtn from '@/components/schedule/ui/AddBtn';
import { CalendarDays } from 'lucide-react';
import Link from 'next/link';
import React from 'react';

export default function page() {
  return (
    <div className="w-full h-full flex flex-col  justify-center">
      <ScheduleListWeb />
      <Link href="/schedule/list">
        <CalendarDays />
      </Link>
      <AddBtn />
    </div>
  );
}
