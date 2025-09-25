'use client';
import { Bell } from 'lucide-react';
import React, { useState } from 'react';

//알림 리스트 추가 필요
export default function BellBtn() {
  const [active, setActive] = useState(false);

  return (
    <Bell
      className={`mr-7 !w-10 !h-10 relative hover:opacity-80 rounded-md p-2 bell-btn ${
        active ? 'active' : ''
      }`}
      onClick={() => setActive((prev) => !prev)}
    />
  );
}
