'use client';
import { useEventDetail, useEventMonth, useScheduleList } from '@/hooks/useSchedule';
import React, { useMemo, useState } from 'react';
import MainCalendar from './calendar/MainCalendar';
import EventList from './list/EventList';
import { getCalendarRange, isEventOnDate, toKey } from '@/utils/date';
import AddBtn from './ui/AddBtn';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { ScheduleEvent } from '@/types/scheduleType';

export default function ScheduleView() {
  const [selected, setSelected] = useState<Date | undefined>(new Date());
  const [displayMonth, setDisplayMonth] = useState<Date>(new Date());
  const matchId = useMatchIdStore((state) => state.matchId);

  const monthRange = useMemo(() => {
    const base = displayMonth;
    const { start, end } = getCalendarRange(base);

    return {
      from: toKey(start),
      to: toKey(end),
    };
  }, [displayMonth]);

  // const { data, isLoading, isError } = useScheduleList(matchId!, {
  //   from: monthRange.from,
  //   to: monthRange.to,
  // });
  const { data: monthEvent } = useEventMonth(matchId!, monthRange.from, monthRange.to);

  const eventId: number | undefined = useMemo(() => {
    //날짜 자르기 yyyy-mm-dd
    const ymd = toKey(selected ?? new Date());
    return monthEvent?.days.find((d) => d.eventAt === ymd)?.eventId;
  }, [monthEvent, selected]);
  const {
    data: detail,
    isLoading: isDetailLoading,
    isError: isDetailError,
  } = useEventDetail(matchId!, eventId!);

  const anniversarySet = useMemo(() => {
    const s = new Set<string>();
    (monthEvent?.days ?? []).forEach((d) => {
      if (d.anniversary) s.add(d.eventAt);
    });
    return s;
  }, [monthEvent]);

  const scheduleSet = useMemo(() => {
    const s = new Set<string>();
    (monthEvent?.days ?? []).forEach((d) => {
      if (!d.anniversary) s.add(d.eventAt);
    });
    return s;
  }, [monthEvent]);

  const dayItems: ScheduleEvent[] = useMemo(() => {
    return detail ? [detail] : [];
  }, [detail]);
  const handleDateSelect = (date: Date | undefined) => {
    if (!date) return;

    setSelected(date);

    if (
      date.getMonth() !== displayMonth.getMonth() ||
      date.getFullYear() !== displayMonth.getFullYear()
    ) {
      setDisplayMonth(new Date(date.getFullYear(), date.getMonth(), 1));
    }
  };

  return (
    <div className="w-full h-full flex justify-center md:rounded-lg md:min-w-[450px] md:max-w-[900px]">
      <div className="relative flex flex-col justify-center w-full h-full">
        <MainCalendar
          selected={selected}
          onSelect={handleDateSelect}
          anniversarySet={anniversarySet}
          scheduleSet={scheduleSet}
          displayMonth={displayMonth}
          onDisplayMonthChange={(d) => setDisplayMonth(new Date(d.getFullYear(), d.getMonth(), 1))}
        />
        <EventList
          date={selected ?? new Date()}
          items={dayItems}
          isLoading={isDetailLoading}
          isError={isDetailError}
        />
        <AddBtn />
      </div>
    </div>
  );
}
