'use client';
import { Button } from '@/components/common/Button';
import { Plus } from 'lucide-react';
import Link from 'next/link';
import React from 'react';

export default function AddBtn() {
  return (
    <Button
      className="w-16 h-16 rounded-full bg-theme-primary flex items-center justify-center absolute bottom-4 right-4"
      asChild
    >
      <Link href="/schedule/register">
        <Plus className="text-secondary" />
      </Link>
    </Button>
  );
}
