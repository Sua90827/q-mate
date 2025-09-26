'use client';

import React from 'react';
import { Calendar } from '../ui/CustomCalendar';
import { toKey } from '@/utils/date';

type Props = {
  selected?: Date;
  onSelect: (date: Date | undefined) => void;
  anniversarySet: Set<string>;
  scheduleSet: Set<string>;
};
export default function MainCalendar({ selected, onSelect, anniversarySet, scheduleSet }: Props) {
  return (
    <div className="flex flex-1 sm:flex-2 justify-center w-full">
      <Calendar
        mode="single"
        defaultMonth={selected}
        selected={selected}
        onSelect={onSelect}
        className="w-full rounded-t-lg"
        components={{
          MonthCaption: ({ calendarMonth }) => {
            const year = calendarMonth.date.getFullYear();
            const month = calendarMonth.date.getMonth() + 1;
            return (
              <div className="flex flex-col items-center mt-2">
                <span className="text-12 text-muted-foreground">{year}</span>
                <span className="text-24 font-semibold">{month}</span>
              </div>
            );
          },
          Day: ({ className, children, day, ...props }) => {
            const key = toKey(day.date);
            const dot = anniversarySet.has(key)
              ? 'bg-anniversary'
              : scheduleSet.has(key)
              ? 'bg-calendar'
              : '';

            return (
              <td {...props} className={`relative p-0 ${className ?? ''}`}>
                {children}
                {dot && (
                  <span
                    aria-hidden
                    className={`pointer-events-none absolute bottom-0 left-1/2 -translate-x-1/2 inline-block w-1.5 h-1.5 rounded-full ${dot}`}
                  />
                )}
              </td>
            );
          },
        }}
        formatters={{
          formatWeekdayName: (date) => {
            return date.toLocaleDateString('en-US', { weekday: 'short' }).toUpperCase();
          },
        }}
      />
    </div>
  );
}
