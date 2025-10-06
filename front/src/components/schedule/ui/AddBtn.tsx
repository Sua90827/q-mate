'use client';
import { Button } from '@/components/common/Button';
import { Plus } from 'lucide-react';
import Link from 'next/link';
import React from 'react';

export default function AddBtn() {
  return (
    <Button
      className="w-16 h-16 rounded-full flex items-center justify-center absolute right-4 bottom-30 sm:bottom-4"
      asChild
    >
      <Link href="/schedule/register">
        <Plus className="text-secondary !w-6 !h-6" />
      </Link>
    </Button>
  );
}
