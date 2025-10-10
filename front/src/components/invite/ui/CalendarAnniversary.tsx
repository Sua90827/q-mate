'use client';

import * as React from 'react';
import { Calendar } from '@/components/ui/calendar';
import { DayPickerProps } from 'react-day-picker';

export default function CalendarAnniversary({
  value,
  onChange,
  disabled,
}: {
  value?: Date;
  onChange?: (d?: Date) => void;
  disabled?: DayPickerProps['disabled'];
}) {
  return (
    <Calendar
      captionLayout="dropdown"
      mode="single"
      defaultMonth={value ?? new Date()}
      selected={value}
      onSelect={onChange}
      disabled={disabled}
      className="rounded-lg border-none shadow-none mx-auto block"
      anniversary
    />
  );
}
