'use client';

import * as React from 'react';
import { Button } from '@/components/ui/button';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import CalendarAnniversary from '../invite/ui/CalendarAnniversary';
import { format } from 'date-fns';

export function DateSelectButton({
  label,
  onSelect,
  isAnniversary = false,
}: {
  label: string;
  onSelect?: (date: string | undefined) => void;
  isAnniversary?: boolean;
}) {
  const birthdateStr = sessionStorage.getItem('birthDate') || '';
  const birthdate = birthdateStr ? new Date(birthdateStr) : undefined;
  const [open, setOpen] = React.useState(false);
  const [date, setDate] = React.useState<Date | undefined>(isAnniversary ? undefined : birthdate);

  const baseStyle =
    'h-[45px] w-[300px] font-bold flex justify-center border rounded-md transition-colors';
  const anniversaryStyle = 'border-gray bg-secondary hover:bg-secondary';
  const normalStyle =
    'w-full border-gray h-[37px] font-semibold bg-secondary justify-between !text-14 hover:bg-secondary';

  const colorClass = isAnniversary
    ? 'text-primary !text-16'
    : date
    ? 'text-text-secondary'
    : 'text-text-secondary/60';

  return (
    <div className="flex flex-col gap-3">
      <Popover open={open} onOpenChange={setOpen}>
        <PopoverTrigger asChild>
          <Button
            id="date"
            className={`${baseStyle} ${
              isAnniversary ? anniversaryStyle : normalStyle
            } ${colorClass}`}
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
