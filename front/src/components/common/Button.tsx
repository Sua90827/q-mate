import * as React from 'react';
import { Slot } from '@radix-ui/react-slot';
import { cva, type VariantProps } from 'class-variance-authority';

import { cn } from '@/lib/utils';

type ThemeCompound = {
  variant: 'default' | 'outline';
  theme: 'day' | 'sunset' | 'night';
  className: string;
};

const themeVariants: ThemeCompound[] = [
  { variant: 'default', theme: 'day', className: 'bg-primary text-secondary hover:bg-primary/80' },
  {
    variant: 'default',
    theme: 'sunset',
    className: 'bg-sunset-active text-secondary hover:bg-sunset-active/80',
  },
  {
    variant: 'default',
    theme: 'night',
    className: 'bg-night-active text-secondary hover:bg-night-active/80',
  },
  {
    variant: 'outline',
    theme: 'day',
    className: 'bg-secondary border border-primary text-primary',
  },
  {
    variant: 'outline',
    theme: 'sunset',
    className: 'bg-secondary border border-sunset-active text-sunset-active ',
  },
  {
    variant: 'outline',
    theme: 'night',
    className: 'bg-secondary border border-night-active text-night-active',
  },
];

const buttonVariants = cva(
  "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-16 font-bold transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive",
  {
    variants: {
      variant: {
        default: 'shadow-xs ',
        outline: 'border-3 shadow-xs hover:bg-accent',
        link: 'text-primary underline-offset-4 hover:underline',
        invite: 'bg-secondary text-primary py-4 shadow-md hover:bg-accent',
      },
      size: {
        default: 'h-12 px-4 has-[>svg]:px-3 font-bold text-[16px]',
        sm: 'h-8 rounded-md gap-1.5 px-3 has-[>svg]:px-2.5',
        lg: 'h-10 rounded-md px-14 has-[>svg]:px-4',
        icon: 'size-9',
      },
      theme: {
        day: '',
        sunset: '',
        night: '',
      },
    },
    compoundVariants: themeVariants,
    defaultVariants: {
      variant: 'default',
      size: 'default',
      theme: 'day',
    },
  },
);

export function Button({
  className,
  variant,
  size,
  theme,
  asChild = false,
  ...props
}: React.ComponentProps<'button'> &
  VariantProps<typeof buttonVariants> & {
    asChild?: boolean;
  }) {
  const Comp = asChild ? Slot : 'button';

  return (
    <Comp
      data-slot="button"
      className={cn(buttonVariants({ variant, size, theme }), className)}
      {...props}
    />
  );
}
