'use client';

import * as React from 'react';
import { Button } from '@/components/ui/button';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import CalendarAnniversary from './CalendarAnniversary';
import { format } from 'date-fns';

export function AnniversaryPicker({
  label,
  onSelect,
}: {
  label: string;
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
            className={` border-gray  bg-secondary hover:bg-secondary h-[45px] w-[300px] !text-primary font-bold flex justify-center text-16`}
          >
            {date ? format(date, 'yyyy-MM-dd') : label}
          </Button>
        </PopoverTrigger>
        <PopoverContent
          className="overflow-hidden p-0"
          align="start"
          style={{ width: 'var(--radix-popover-trigger-width)' }}
        >
          <CalendarAnniversary
            value={date}
            onChange={(d) => {
              setDate(d);
              onSelect?.(d ? d.toISOString() : undefined);
              setOpen(false);
            }}
            disabled={(d) => d > new Date()}
          />
        </PopoverContent>
      </Popover>
    </div>
  );
}
