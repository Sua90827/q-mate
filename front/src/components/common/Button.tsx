'use client';

import * as React from 'react';
import { Slot } from '@radix-ui/react-slot';
import { cva, type VariantProps } from 'class-variance-authority';

import { cn } from '@/lib/utils';

const buttonVariants = cva(
  "inline-flex items-center justify-center gap-2 whitespace-nowrap rounded-md text-16 font-bold transition-all disabled:pointer-events-none disabled:opacity-50 [&_svg:not([class*='size-'])]:size-4 shrink-0 [&_svg]:shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px]",
  {
    variants: {
      variant: {
        default: 'shadow-xs btn-default',
        outline: 'border-2 shadow-xs hover:bg-accent btn-outline',
        invite: 'bg-secondary text-primary py-4 shadow-md hover:bg-accent btn-invite',
        primary: 'bg-primary text-secondary shadow-xs hover:opacity-80',
        primaryOutline:
          'border-2 shadow-xs hover:bg-accent bg-secondary border-primary text-primary',
        dangerPrimary: 'bg-error text-secondary shadow-xs hover:opacity-80',
        dangerOutline: 'bg-secondary text-error border-2 border-error shadow-xs hover:bg-accent',
        icon: 'hover:opacity-80',
      },
      size: {
        default: 'h-12 px-4 has-[>svg]:px-3 font-bold text-[16px]',
        sm: 'h-8 rounded-md gap-1.5 px-3 has-[>svg]:px-2.5',
        lg: 'h-10 rounded-md px-14 has-[>svg]:px-4',
        icon: 'size-9',
      },
    },
    defaultVariants: {
      variant: 'default',
      size: 'default',
    },
  },
);

export interface ButtonProps
  extends React.ButtonHTMLAttributes<HTMLButtonElement>,
    VariantProps<typeof buttonVariants> {
  asChild?: boolean;
}

export function Button({ className, variant, size, asChild = false, ...props }: ButtonProps) {
  const Comp = asChild ? Slot : 'button';

  return (
    <Comp
      data-slot="button"
      className={cn(buttonVariants({ variant, size }), className)}
      {...props}
    />
  );
}
