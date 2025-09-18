import { Plus } from 'lucide-react';
import Link from 'next/link';
import React from 'react';

export default function AddBtn() {
  return (
    <Link
      href="/schedule/register"
      className="bg-primary w-16 h-16 rounded-full flex items-center justify-center absolute bottom-4 right-4"
    >
      <Plus className="text-secondary" />
    </Link>
  );
}
