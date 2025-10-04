'use client';
import React from 'react';
import { usePetStateStore } from '@/store/usePetStore';

export default function TestBtn() {
  const addExp = usePetStateStore((s) => s.addExp);

  return (
    <button onClick={() => addExp(10)} className="px-4 py-2 bg-blue-500 text-white rounded">
      +10 EXP 테스트
    </button>
  );
}
