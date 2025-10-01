import { CheckCircle, XCircle } from 'lucide-react';
import React from 'react';
import { toast } from 'sonner';

export function SuccessToast(message?: string) {
  return toast.custom(() => (
    <div className="flex items-center gap-2 rounded-lg border border-theme-primary bg-theme-primary/10 px-4 py-3 text-theme-primary shadow-lg">
      <CheckCircle className="h-5 w-5 text-theme-primary" />
      <span className="font-medium whitespace-pre-line">{message}</span>
    </div>
  ));
}
export function ErrorToast(message?: string) {
  return toast.custom(() => (
    <div className="flex items-center gap-2 rounded-lg border border-red-500 bg-red-500/10 px-4 py-3 text-red-600 shadow-lg">
      <XCircle className="h-5 w-5 text-red-600" />
      <span className="font-medium whitespace-pre-line">{message}</span>
    </div>
  ));
}
