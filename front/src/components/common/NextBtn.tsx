import React from 'react';
import { Button } from './Button';
import { ChevronRight } from 'lucide-react';

export default function NextBtn({
  page,
  setPage,
  totalPages,
}: {
  page: number;
  setPage: React.Dispatch<React.SetStateAction<number>>;
  totalPages: number;
}) {
  return (
    <Button
      disabled={page + 1 >= totalPages}
      onClick={() => setPage((p) => p + 1)}
      className="mx-3 disabled:opacity-50"
      variant={'icon'}
    >
      <ChevronRight />
    </Button>
  );
}
