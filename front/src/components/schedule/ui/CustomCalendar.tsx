'use client';

import * as React from 'react';
import { ChevronDownIcon, ChevronLeftIcon, ChevronRightIcon } from 'lucide-react';
import { type DayButton, DayPicker, getDefaultClassNames } from 'react-day-picker';

import { cn } from '@/lib/utils';
import { Button, buttonVariants } from '@/components/ui/button';
// MainCalendar용 calendar ui
function Calendar({
  className,
  classNames,
  showOutsideDays = true,
  captionLayout = 'label',
  buttonVariant = 'ghost',
  formatters,
  components,
  ...props
}: React.ComponentProps<typeof DayPicker> & {
  buttonVariant?: React.ComponentProps<typeof Button>['variant'];
}) {
  const defaultClassNames = getDefaultClassNames();

  return (
    <DayPicker
      showOutsideDays={showOutsideDays}
      className={cn(
        'bg-background group/calendar p-4 [--cell-size:--spacing(9)] [[data-slot=card-content]_&]:bg-transparent [[data-slot=popover-content]_&]:bg-transparent',
        String.raw`rtl:**:[.rdp-button\_next>svg]:rotate-180`,
        String.raw`rtl:**:[.rdp-button\_previous>svg]:rotate-180`,
        className,
      )}
      captionLayout={captionLayout}
      formatters={{
        formatMonthDropdown: (date) => date.toLocaleString('default', { month: 'short' }),
        ...formatters,
      }}
      classNames={{
        month_grid: cn('h-full', defaultClassNames.month_grid),
        root: cn('w-fit', defaultClassNames.root),
        months: cn('flex gap-4 flex-col md:flex-row relative', defaultClassNames.months),
        month: cn('flex flex-col w-full gap-4', defaultClassNames.month),
        nav: cn(
          'flex items-center gap-1 w-full absolute top-4 inset-x-0 justify-between text-gray-700',
          defaultClassNames.nav,
        ),
        button_previous: cn(
          buttonVariants({ variant: buttonVariant }),
          'size-(--cell-size) aria-disabled:opacity-50 p-0 select-none cursor-pointer',
          defaultClassNames.button_previous,
        ),
        button_next: cn(
          buttonVariants({ variant: buttonVariant }),
          'size-(--cell-size) aria-disabled:opacity-50 p-0 select-none cursor-pointer',
          defaultClassNames.button_next,
        ),
        month_caption: cn(
          'flex items-center justify-center h-(--cell-size) w-full px-(--cell-size)',
          defaultClassNames.month_caption,
        ),
        dropdowns: cn(
          'w-full flex items-center text-sm font-medium justify-center h-(--cell-size) gap-1.5',
          defaultClassNames.dropdowns,
        ),
        dropdown_root: cn(
          'relative has-focus:border-ring border border-input shadow-xs has-focus:ring-ring/50 has-focus:ring-[3px] rounded-md',
          defaultClassNames.dropdown_root,
        ),
        dropdown: cn('absolute bg-popover inset-0 opacity-0', defaultClassNames.dropdown),
        caption_label: cn(
          'select-none font-medium',
          captionLayout === 'label'
            ? 'text-sm'
            : 'rounded-md pl-2 pr-1 flex items-center gap-1 text-sm h-8 [&>svg]:text-muted-foreground [&>svg]:size-3.5',
          defaultClassNames.caption_label,
        ),
        table: 'w-full border-collapse',
        weekdays: cn(
          `grid grid-cols-7 [&>*:first-child]:text-red-400 [&>*:last-child]:text-blue-400`,
          defaultClassNames.weekdays,
        ),
        weekday: cn(
          'text-muted-foreground font-normal text-14 text-text-secondary select-none h-9 grid place-items-center font-semibold',
          defaultClassNames.weekday,
        ),
        week: cn(
          'grid grid-cols-7 w-full place-items-center justify-center [&>*:nth-child(7n+1)]:text-red-400 [&>*:nth-child(7n)]:text-blue-400 ',
          defaultClassNames.week,
        ),
        week_number_header: cn('select-none w-(--cell-size)', defaultClassNames.week_number_header),
        week_number: cn(
          'text-[0.8rem] select-none text-muted-foreground ',
          defaultClassNames.week_number,
        ),
        day: cn(
          'relative p-0 text-center group/day select-none grid place-items-center w-full h-[60px] lg:aspect-auto lg:h-[65px]  ',
          defaultClassNames.day,
        ),
        range_start: cn('bg-accent', defaultClassNames.range_start),
        range_middle: cn('bg-accent', defaultClassNames.range_middle),
        range_end: cn('bg-accent', defaultClassNames.range_end),
        today: cn(
          'data-[selected=true]:rounded-full data-[selected=true]:text-16',
          defaultClassNames.today,
        ),
        outside: cn('opacity-40 aria-selected:text-muted-foreground', defaultClassNames.outside),
        disabled: cn('text-muted-foreground opacity-50', defaultClassNames.disabled),
        hidden: cn('invisible', defaultClassNames.hidden),
        ...classNames,
      }}
      components={{
        Root: ({ className, rootRef, ...props }) => {
          return <div data-slot="calendar" ref={rootRef} className={cn(className)} {...props} />;
        },
        Chevron: ({ className, orientation, ...props }) => {
          if (orientation === 'left') {
            return <ChevronLeftIcon className={cn('size-4', className)} {...props} />;
          }

          if (orientation === 'right') {
            return <ChevronRightIcon className={cn('size-4', className)} {...props} />;
          }

          return <ChevronDownIcon className={cn('size-4', className)} {...props} />;
        },
        DayButton: CalendarDayButton,
        WeekNumber: ({ children, ...props }) => {
          return (
            <td {...props}>
              <div className="flex size-(--cell-size) items-center justify-center text-center ">
                {children}
              </div>
            </td>
          );
        },
        ...components,
      }}
      {...props}
    />
  );
}

function CalendarDayButton({
  className,
  day,
  modifiers,
  ...props
}: React.ComponentProps<typeof DayButton>) {
  const defaultClassNames = getDefaultClassNames();

  const ref = React.useRef<HTMLButtonElement>(null);
  React.useEffect(() => {
    if (modifiers.focused) ref.current?.focus();
  }, [modifiers.focused]);

  return (
    <Button
      ref={ref}
      variant="ghost"
      size="icon"
      data-day={day.date.toLocaleDateString()}
      data-selected-single={
        modifiers.selected &&
        !modifiers.range_start &&
        !modifiers.range_end &&
        !modifiers.range_middle
      }
      data-range-start={modifiers.range_start}
      data-range-end={modifiers.range_end}
      data-range-middle={modifiers.range_middle}
      className={cn(
        'cursor-pointer',
        ' data-[selected-single=true]:text-theme-primary  data-[selected-single=true]:text-16  data-[selected-single=true]:font-semibold',

        'flex w-8 h-8 lg:w-12 lg:h-12 p-0 flex-col gap-1 leading-none font-normal',
        'rounded-full',
        'data-[selected-single=true]:rounded-full',
        '[&>span]:text-xs [&>span]:opacity-70',
        'items-center justify-center mx-auto',
        'focus:outline-none focus-visible:outline-none text-16',

        defaultClassNames.day_button,
        className,
      )}
      {...props}
    />
  );
}

export { Calendar, CalendarDayButton };
