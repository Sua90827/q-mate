'use client';
import { useScheduleList } from '@/hooks/useSchedule';
import React, { useMemo, useState } from 'react';
import MainCalendar from './calendar/MainCalendar';
import EventList from './list/EventList';
import { isEventOnDate, toKey } from '@/utils/date';
import AddBtn from './ui/AddBtn';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { ScheduleEvent } from '@/types/scheduleType';

export default function ScheduleView() {
  const [selected, setSelected] = useState<Date | undefined>(new Date());
  const matchId = useMatchIdStore((state) => state.matchId);

  const monthRange = useMemo(() => {
    const base = selected ?? new Date();

    // 첫째날
    const start = new Date(base.getFullYear(), base.getMonth(), 1);
    // 마지막날
    const end = new Date(base.getFullYear(), base.getMonth() + 1, 0);

    return {
      from: toKey(start),
      to: toKey(end),
    };
  }, [selected]);

  const { data, isLoading, isError } = useScheduleList(matchId!, {
    from: monthRange.from,
    to: monthRange.to,
  });

  const anniversarySet = useMemo(() => {
    const s = new Set<string>();
    (data?.content ?? []).forEach((e) => {
      if (e.isAnniversary) s.add(e.eventAt);
    });
    return s;
  }, [data]);

  const scheduleSet = useMemo(() => {
    const s = new Set<string>();
    (data?.content ?? []).forEach((e) => {
      if (!e.isAnniversary && !anniversarySet.has(e.eventAt)) s.add(e.eventAt);
    });
    return s;
  }, [data, anniversarySet]);

  console.log(data);

  const dayItems: ScheduleEvent[] = useMemo(() => {
    const list = data?.content ?? [];
    return list.filter((event) => isEventOnDate(event, selected ?? new Date()));
  }, [data, selected]);

  return (
    <div className="w-full h-full flex justify-center md:rounded-lg md:min-w-[450px] md:max-w-[900px]">
      <div className="relative flex flex-col justify-center w-full h-full">
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
