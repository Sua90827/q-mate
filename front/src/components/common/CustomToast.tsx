import { CheckCircle, XCircle } from 'lucide-react';
import React from 'react';
import { toast } from 'sonner';
import { cn } from '@/lib/utils';
import Image from 'next/image';

export function SuccessToast(message?: string, className?: string) {
  return toast.custom(() => (
    <div
      className={cn(
        'inline-flex items-center gap-2 rounded-lg border border-theme-primary bg-secondary px-4 py-3 text-theme-accent shadow-lg',
        className,
      )}
    >
      <CheckCircle className={cn('h-5 w-5 text-theme-accent', className)} />
      <span className="font-medium  whitespace-nowrap">{message}</span>
    </div>
  ));
}
export function ErrorToast(message?: string, className?: string) {
  return toast.custom(() => (
    <div
      className={cn(
        'flex items-center gap-2 rounded-lg border border-red-600 bg-secondary px-4 py-3 text-red-600 shadow-lg',
        className,
      )}
    >
      <XCircle className={cn('h-5 w-5 text-red-600', className)} />
      <span className="font-medium whitespace-pre-line">{message}</span>
    </div>
  ));
}
export function pushToast(title?: string, body?: string, icon?: string, onClick?: () => void) {
  return toast.custom(
    () => (
      <div
        onClick={onClick}
        className={cn(
          'flex  items-center gap-4 rounded-lg border border-theme-primary bg-secondary px-4 py-3 text-theme-accent shadow-lg cursor-pointer',
        )}
      >
        <Image src={icon || '/favicon2.svg'} alt="logo" width={30} height={30} />

        <div className="flex flex-col">
          <span className="font-bold text-16 whitespace-pre-line">{title}</span>
          <span className="font-medium text-14 whitespace-pre-line">{body}</span>
        </div>
      </div>
    ),
    { duration: 4000 },
  );
}
