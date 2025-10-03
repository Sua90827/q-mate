import React from 'react';
import { Button } from './Button';
import { X } from 'lucide-react';

export default function CloseButton({ onClick }: { onClick?: () => void }) {
  return (
    <Button
      variant={'icon'}
      className="text-theme-primary"
      onClick={onClick}
      aria-label="목록으로"
      type="button"
    >
      <X className="!w-6 !h-6" />
    </Button>
  );
}
