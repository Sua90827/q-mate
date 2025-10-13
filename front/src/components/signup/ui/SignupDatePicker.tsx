'use client';

import { Controller, Control } from 'react-hook-form';
import { SignupFormValues } from '@/utils/validation/signupValidation';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/ui/popover';
import { Button } from '@/components/ui/button';
import { Calendar } from '@/components/ui/calendar';

import { cn } from '@/lib/utils';
import { format } from 'date-fns';
import { dateToString } from '@/utils/date';

type Props = {
  control: Control<SignupFormValues>;
};

export default function SignupDatePicker({ control }: Props) {
  return (
    <Controller
      name="birth"
      control={control}
      render={({ field }) => {
        const selectedDate = field.value ? new Date(field.value) : undefined;

        return (
          <Popover>
            <PopoverTrigger asChild>
              <Button
                type="button"
                variant="outline"
                className={cn(
                  'h-11 w-full justify-start rounded-md !bg-secondary !text-muted-foreground p-3 !text-14 text- font-semibold',
                  !field.value && '!text-dash text-14',
                )}
              >
                {field.value ? format(selectedDate as Date, 'yyyy-MM-dd') : '생년월일'}
              </Button>
            </PopoverTrigger>
            <PopoverContent
              className="w-auto p-0 flex justify-center items-center"
              align="start"
              style={{ width: 'var(--radix-popover-trigger-width)' }}
            >
              <Calendar
                mode="single"
                captionLayout="dropdown"
                selected={selectedDate}
                onSelect={(d) => {
                  if (d) field.onChange(dateToString(d));
                }}
                disabled={(d) => d > new Date()}
              />
            </PopoverContent>
          </Popover>
        );
      }}
    />
  );
}
