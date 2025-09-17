import { Share2 } from 'lucide-react';
import React from 'react';

export default function ShareBtn() {
  return (
    <span className="bg-primary w-16 h-16 rounded-full flex items-center justify-center absolute bottom-4 right-4">
      <Share2 className="text-secondary" />
    </span>
  );
}
