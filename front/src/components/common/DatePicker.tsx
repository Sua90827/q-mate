'use client';

import * as React from 'react';
import { ChevronDownIcon } from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import Calendar from '../schedule/CalendarCustom';

export function DatePicker({ label, schedule }: { label: string; schedule?: boolean }) {
  const [open, setOpen] = React.useState(false);
  const [date, setDate] = React.useState<Date | undefined>(undefined);

  return (
    <div className="flex flex-col gap-3">
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            id="date"
            className={`w-full border-gray h-[37px]   font-semibold bg-secondary justify-between  text-14 hover:bg-secondary ${
              schedule ? 'shadow-box  ' : '!text-text-secondary/60'
            }`}
          >
            {date ? date.toLocaleDateString() : label}
            <ChevronDownIcon />
          </Button>
        </PopoverTrigger>
        <PopoverContent
          className="overflow-hidden p-0"
          align="start"
          style={{ width: 'var(--radix-popover-trigger-width)' }}
        >
          <Calendar
            value={date}
            onChange={(d) => {
              setDate(d);
              setOpen(false);
            }}
          />
        </PopoverContent>
      </Popover>
    </div>
  );
}
