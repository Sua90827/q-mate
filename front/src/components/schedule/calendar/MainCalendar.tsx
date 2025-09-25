'use client';

import React from 'react';
import { Calendar } from '../ui/CustomCalendar';
import { Schedule } from '@/types/scheduleType';

type Props = {
  selected?: Date;
  onSelect: (date: Date | undefined) => void;
  getDayEvents: (date: Date) => Schedule[];
};
export default function MainCalendar({ selected, onSelect, getDayEvents }: Props) {
  return (
    <div className="flex flex-1.5 justify-center rounded-lg border shadow-sm w-full md:w-[500px]">
      <Calendar
        mode="single"
        defaultMonth={selected}
        selected={selected}
        onSelect={onSelect}
        className="w-full"
        components={{
          MonthCaption: ({ calendarMonth }) => {
            const year = calendarMonth.date.getFullYear();
            const month = calendarMonth.date.getMonth() + 1;
            return (
              <div className="flex flex-col items-center">
                <span className="text-12 text-muted-foreground">{year}</span>
                <span className="text-24 font-semibold">{month}</span>
              </div>
            );
          },
        }}
      />
    </div>
  );
}
