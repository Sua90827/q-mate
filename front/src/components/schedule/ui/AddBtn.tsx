'use client';
import { Button } from '@/components/common/Button';
import { useThemeStore } from '@/store/useThemeStore';
import { Plus } from 'lucide-react';
import Link from 'next/link';
import React from 'react';

export default function AddBtn() {
  const theme = useThemeStore((state) => state.theme);

  return (
    <Button
      className="w-16 h-16 rounded-full flex items-center justify-center absolute bottom-4 right-4"
      asChild
      theme={theme}
    >
      <Link href="/schedule/register">
        <Plus className="text-secondary" />
      </Link>
    </Button>
  );
}
