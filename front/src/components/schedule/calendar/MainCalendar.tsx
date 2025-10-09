'use client';

import React from 'react';
import { Calendar, CalendarDayButton } from '../ui/CustomCalendar';
import { toKey } from '@/utils/date';
import { cn } from '@/lib/utils';

type Props = {
  selected?: Date;
  onSelect: (date: Date | undefined) => void;
  anniversarySet: Set<string>;
  scheduleSet: Set<string>;
  displayMonth: Date;
  onDisplayMonthChange: (date: Date) => void;
};
export default function MainCalendar({
  selected,
  onSelect,
  anniversarySet,
  scheduleSet,
  displayMonth,
  onDisplayMonthChange,
}: Props) {
  return (
    <div className="flex flex-1 justify-center w-full">
      <Calendar
        mode="single"
        // defaultMonth={selected}
        month={displayMonth}
        onMonthChange={onDisplayMonthChange}
        selected={selected}
        onSelect={onSelect}
        className="w-full md:rounded-t-lg theme-night:bg-black"
        components={{
          MonthCaption: ({ calendarMonth }) => {
            const year = calendarMonth.date.getFullYear();
            const month = calendarMonth.date.getMonth() + 1;
            return (
              <div className="flex flex-col items-center mt-3">
                <span className="text-14 text-muted-foreground">{year}</span>
                <span className="text-24 font-semibold">{month}</span>
              </div>
            );
          },
          DayButton: (props) => {
            const isSingleSelected = props.modifiers?.selected;
            const isToday = props.modifiers?.today;
            return (
              <CalendarDayButton
                {...props}
                className={cn(
                  props.className,
                  isSingleSelected
                    ? 'bg-calendar '
                    : isToday && 'bg-gray-200 hover:bg-gray-200 text-theme-secondary text-16',
                )}
              />
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
              <td {...props} className={`h-20 relative p-0 ${className ?? ''}`}>
                {children}

                {dot && (
                  <span
                    aria-hidden
                    className={`pointer-events-none absolute bottom-0 left-1/2 -translate-x-1/2 inline-block w-2 h-2 rounded-full  ${dot}`}
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
