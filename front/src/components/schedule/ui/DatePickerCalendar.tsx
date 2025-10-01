'use client';

import * as React from 'react';
import { Calendar } from '@/components/ui/calendar';

export default function DatePickerCalendar({
  value,
  onChange,
}: {
  value?: Date;
  onChange?: (d?: Date) => void;
}) {
  return (
    <Calendar
      mode="single"
      defaultMonth={value ?? new Date()}
      selected={value}
      onSelect={onChange}
      className="rounded-lg border-none shadow-none mx-auto block "
    />
  );
}
