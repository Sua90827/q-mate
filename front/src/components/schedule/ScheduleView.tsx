'use client';
import { useEventMonth, useScheduleList } from '@/hooks/useSchedule';
import React, { useMemo, useState } from 'react';
import MainCalendar from './calendar/MainCalendar';
import EventList from './list/EventList';
import { Schedule } from '@/types/scheduleType';
import { isEventOnDate } from '@/utils/date';
import AddBtn from './ui/AddBtn';

export default function ScheduleView() {
  const [selected, setSelected] = useState<Date | undefined>(new Date());
  const { data, isLoading, isError } = useScheduleList();

  const dayItems = useMemo(() => {
    const list: Schedule[] = data ?? [];
    return list.filter((event) => isEventOnDate(event, selected ?? new Date()));
  }, [data, selected]);

  return (
    <div className="w-full h-full flex justify-center">
      <div className="relative flex flex-col justify-center rounded-lg border shadow-sm w-full sm:w-[500px] h-full">
        <MainCalendar selected={selected} onSelect={(date) => date && setSelected(date)} />
        <EventList
          date={selected ?? new Date()}
          items={dayItems}
          isLoading={isLoading}
          isError={isError}
        />
        <AddBtn />
      </div>
    </div>
  );
}
