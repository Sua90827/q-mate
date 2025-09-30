import React from 'react';
import { Button } from './Button';
import { ChevronLeft } from 'lucide-react';

export default function PrevBtn({
  page,
  setPage,
}: {
  page: number;
  setPage: React.Dispatch<React.SetStateAction<number>>;
}) {
  return (
    <Button
      disabled={page === 0}
      onClick={() => setPage((p) => Math.max(p - 1, 0))}
      className="mx-3 disabled:opacity-50"
      variant={'icon'}
    >
      <ChevronLeft />
    </Button>
  );
}
