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

  const currentMonth = selected ? selected.getMonth() + 1 : new Date().getMonth() + 1;
  const currentYear = selected ? selected.getFullYear() : new Date().getFullYear();
  const { data: monthData } = useEventMonth(currentYear, currentMonth);

  const anniversarySet = useMemo(() => {
    const s = new Set<string>();
    monthData?.days.forEach((d) => {
      if (d.isAnniversary) s.add(d.eventAt);
    });
    return s;
  }, [monthData]);

  const scheduleSet = useMemo(() => {
    const s = new Set<string>();
    monthData?.days.forEach((d) => {
      if (!d.isAnniversary && !anniversarySet.has(d.eventAt)) s.add(d.eventAt);
    });
    return s;
  }, [monthData, anniversarySet]);

  const { data, isLoading, isError } = useScheduleList();

  const dayItems = useMemo(() => {
    const list: Schedule[] = data ?? [];
    return list.filter((event) => isEventOnDate(event, selected ?? new Date()));
  }, [data, selected]);

  return (
    <div className="w-full h-full flex justify-center rounded-lg">
      <div className="relative flex flex-col justify-center rounded-lg w-full h-full sm:w-[500px] md:w-[600px] lg:w-[700px]">
        <MainCalendar
          selected={selected}
          onSelect={(date) => date && setSelected(date)}
          anniversarySet={anniversarySet}
          scheduleSet={scheduleSet}
        />
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
