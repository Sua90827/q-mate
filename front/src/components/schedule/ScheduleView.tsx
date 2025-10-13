'use client';
import { useEventMonth, useScheduleList } from '@/hooks/useSchedule';
import React, { useMemo, useState } from 'react';
import MainCalendar from './calendar/MainCalendar';
import EventList from './list/EventList';
import { dateToString, getCalendarRange } from '@/utils/date';
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
      from: dateToString(start),
      to: dateToString(end),
    };
  }, [displayMonth]);

  const { data: monthEvent } = useEventMonth(matchId!, monthRange.from, monthRange.to);

  const dayRange = useMemo(() => {
    const ymd = dateToString(selected ?? new Date());
    return { from: ymd, to: ymd };
  }, [selected]);

  const {
    data: dayList,
    isLoading: isDayLoading,
    isError: isDayError,
  } = useScheduleList(matchId!, dayRange);

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

  // 리스트 표시용
  const dayItems: ScheduleEvent[] = useMemo(() => {
    const list = dayList?.content ?? [];
    return list;
  }, [dayList]);
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
          onDisplayMonthChange={(d) => {
            const firstDay = new Date(d.getFullYear(), d.getMonth(), 1);
            setDisplayMonth(firstDay);
            setSelected(firstDay);
          }}
        />
        <EventList
          date={selected ?? new Date()}
          items={dayItems}
          isLoading={isDayLoading}
          isError={isDayError}
        />
        <AddBtn />
      </div>
    </div>
  );
}
