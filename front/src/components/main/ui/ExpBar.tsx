'use client';

import * as React from 'react';
import * as SliderPrimitive from '@radix-ui/react-slider';

import { cn } from '@/lib/utils';
import { usePetStateStore } from '@/store/usePetStore';

export function ExpBar({
  className,
  defaultValue,
  min = 0,
  max = 1000,
  step = 10,
  ...props
}: React.ComponentProps<typeof SliderPrimitive.Root>) {
  const currentExp = usePetStateStore((state) => state.currentExp); //현재 조회한 exp
  return (
    <div className="flex flex-col">
      <span className="font-Gumi pb-2 text-theme-primary">EXP</span>

      <SliderPrimitive.Root
        data-slot="slider"
        defaultValue={defaultValue}
        value={[currentExp]}
        min={min}
        max={max}
        step={step}
        className={cn(
          'relative !h-[20px] flex !w-[250px] touch-none items-center select-none data-[disabled]:opacity-50 data-[orientation=vertical]:h-full data-[orientation=vertical]:min-h-44 data-[orientation=vertical]:w-auto data-[orientation=vertical]:flex-col pointer-events-none',
          className,
        )}
        {...props}
      >
        <SliderPrimitive.Track
          data-slot="slider-track"
          className={cn(
            'bg-secondary opacity-80 !h-[20px] relative grow overflow-hidden rounded-full data-[orientation=horizontal]:h-1.5 data-[orientation=horizontal]:w-full data-[orientation=vertical]:h-full data-[orientation=vertical]:w-1.5',
          )}
        >
          <SliderPrimitive.Range
            data-slot="slider-range"
            className={cn(
              'bg-exp absolute data-[orientation=horizontal]:h-full data-[orientation=vertical]:w-full',
            )}
          />
        </SliderPrimitive.Track>
      </SliderPrimitive.Root>
    </div>
  );
}
