import { CheckCircle, XCircle } from 'lucide-react';
import React from 'react';
import { toast } from 'sonner';
import { cn } from '@/lib/utils';

export function SuccessToast(message?: string, className?: string) {
  return toast.custom(() => (
    <div
      className={cn(
        'flex items-center gap-2 rounded-lg border border-theme-primary bg-theme-primary/10 px-4 py-3 text-theme-accent shadow-lg',
        className,
      )}
    >
      <CheckCircle className={cn('h-5 w-5 text-theme-primary', className)} />
      <span className="font-medium whitespace-pre-line">{message}</span>
    </div>
  ));
}
export function ErrorToast(message?: string, className?: string) {
  return toast.custom(() => (
    <div
      className={cn(
        'flex items-center gap-2 rounded-lg border border-red-500 bg-red-500/10 px-4 py-3 text-red-600 shadow-lg',
        className,
      )}
    >
      <XCircle className={cn('h-5 w-5 text-red-600', className)} />
      <span className="font-medium whitespace-pre-line">{message}</span>
    </div>
  ));
}
