import { Loader2 } from 'lucide-react';
import React from 'react';

export default function Loader() {
  return (
    <div className="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
      <Loader2 className="h-24 w-24 animate-spin text-secondary" />
    </div>
  );
}
