'use client';
import { Bell } from 'lucide-react';
import React, { useState } from 'react';

export default function BellBtn() {
  const [active, setActive] = useState(false);

  return (
    <Bell
      className={`mr-7 !w-10 !h-10 relative  hover:bg-primary hover:text-secondary hover:rounded-md p-2 ${
        active ? 'rounded-md bg-primary text-secondary' : ''
      }`}
      onClick={() => setActive((prev) => !prev)}
    />
  );
}
