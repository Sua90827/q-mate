import React from 'react';
import { Button } from './Button';
import { X } from 'lucide-react';

export default function DeleteBtn({
  onClick,
}: {
  onClick?: React.MouseEventHandler<HTMLButtonElement>;
}) {
  return (
    <Button
      className="text-text-secondary !h-5 px-1 py-3"
      variant={'icon'}
      onClick={onClick}
      aria-label="삭제"
      type="button"
    >
      <X className="!w-5 !h-5 " />
    </Button>
  );
}
