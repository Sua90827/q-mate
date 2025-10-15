'use client';

import { createPortal } from 'react-dom';
import { Toaster } from '@/components/ui/sonner';

export default function GlobalToaster() {
  if (typeof window === 'undefined') return null;
  return createPortal(
    <Toaster position="top-center" offset={100} visibleToasts={1} />,
    document.body,
  );
}
