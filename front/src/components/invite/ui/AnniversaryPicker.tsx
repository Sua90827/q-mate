'use client';

import * as React from 'react';
import { Button } from '@/components/ui/button';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import Calendar from '@/components/schedule/CalendarCustom';
import { useRouter } from 'next/navigation';

export function AnniversaryPicker({
  label,
  onSelect,
}: {
  label: string;
  onSelect?: (date: string | undefined) => void;
}) {
  const [open, setOpen] = React.useState(false);
  const [date, setDate] = React.useState<Date | undefined>(undefined);
  const router = useRouter();
  if (onSelect !== undefined) {
    router.push('invite/invite');
  }

  return (
    <div className="flex flex-col gap-3">
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            id="date"
            className={` border-gray  bg-secondary hover:bg-secondary h-[45px] w-[300px] !text-primary font-bold flex justify-center text-16`}
          >
            {date ? date.toLocaleDateString() : label}
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
              onSelect?.(d ? d.toISOString() : undefined);
              setOpen(false);
            }}
          />
        </PopoverContent>
      </Popover>
    </div>
  );
}
