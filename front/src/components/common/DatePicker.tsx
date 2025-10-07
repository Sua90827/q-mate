'use client';

import * as React from 'react';
import { Button } from '@/components/ui/button';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import DatePickerCalendar from '../schedule/ui/DatePickerCalendar';
import { toKey } from '@/utils/date';

export function DatePicker({
  label,
  schedule,
  onSelect,
  initialDate,
}: {
  label: string;
  schedule?: boolean;
  onSelect?: (date: string | undefined) => void;
  initialDate?: string;
}) {
  const initial = initialDate ? new Date(initialDate) : undefined;
  const [open, setOpen] = React.useState(false);
  const [date, setDate] = React.useState<Date | undefined>(initial);

  const formatDate = (d: Date) => d.toISOString().split('T')[0];

  return (
    <div className="flex flex-col gap-3">
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            id="date"
            className={`w-full border-gray h-[45px] font-semibold bg-secondary justify-between !text-14 hover:bg-secondary
            ${schedule ? 'shadow-box' : ''}
            ${!date ? 'text-text-secondary/80' : 'text-text-primary'}
            `}
          >
            {date ? toKey(date) : label}
          </Button>
        </PopoverTrigger>
        <PopoverContent
          className="overflow-hidden p-0"
          align="start"
          style={{ width: 'var(--radix-popover-trigger-width)' }}
        >
          <DatePickerCalendar
            value={date}
            onChange={(d: Date | undefined) => {
              setDate(d);
              onSelect?.(d ? formatDate(d) : undefined);
              setOpen(false);
            }}
          />
        </PopoverContent>
      </Popover>
    </div>
  );
}
