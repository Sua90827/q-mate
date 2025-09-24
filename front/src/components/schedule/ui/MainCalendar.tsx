'use client';

import React, { useState } from 'react';
import type { DateRange } from 'react-day-picker';
import { Calendar } from './CustomCalendar';

export default function MainCalendar() {
  const [date, setDate] = useState<Date | undefined>(new Date());

  return (
    <div className="flex justify-center rounded-lg border shadow-sm w-full md:w-[500px]">
      <Calendar
        mode="single"
        defaultMonth={date}
        selected={date}
        onSelect={setDate}
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
