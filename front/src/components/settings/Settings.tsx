'use client';
import React from 'react';
import BellBtn from '../common/BellBtn';

export default function Settings() {
  return (
    <div className="w-full min-h-screen">
      <div className="absolute top-0 left-0 right-0 flex items-center justify-between py-5 sm:hidden px-4">
        <div className="w-6" />
        <span className="flex-1 font-Gumi text-center text-20">설정</span>
        <BellBtn />
      </div>
    </div>
  );
}
