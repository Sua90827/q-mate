import ScheduleListWeb from '@/components/schedule/ScheduleListWeb';
import AddBtn from '@/components/schedule/ui/AddBtn';
import { CalendarDays, Link } from 'lucide-react';
import React from 'react';

export default function page() {
  return (
    <div className="bg-gradient-sub w-full min-h-screen flex flex-col  justify-center">
      <ScheduleListWeb />
      <Link href="/schedule/list">
        <CalendarDays />
      </Link>
      <AddBtn />
    </div>
  );
}
