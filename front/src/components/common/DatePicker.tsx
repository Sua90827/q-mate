'use client';

import * as React from 'react';
import { Button } from '@/components/ui/button';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import CalendarCustom from '../schedule/ui/CalendarCustom';

export function DatePicker({
  label,
  schedule,
  onSelect,
}: {
  label: string;
  schedule?: boolean;
  onSelect?: (date: string | undefined) => void;
}) {
  const [open, setOpen] = React.useState(false);
  const [date, setDate] = React.useState<Date | undefined>(undefined);

  return (
    <div className="flex flex-col gap-3">
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            id="date"
            className={`w-full border-gray h-[37px] font-semibold bg-secondary justify-between  text-14 hover:bg-secondary ${
              schedule ? 'shadow-box  ' : '!text-text-secondary/60'
            } `}
          >
            {date ? date.toLocaleDateString() : label}
          </Button>
        </PopoverTrigger>
        <PopoverContent
          className="overflow-hidden p-0"
          align="start"
          style={{ width: 'var(--radix-popover-trigger-width)' }}
        >
          <CalendarCustom
            value={date}
            onChange={(d: Date | undefined) => {
              setDate(d);
              onSelect?.(d ? d.toISOString() : undefined);
              setOpen(false);
            }}
          />
        </PopoverContent>
      </Popover>
    </div>
  );
}
