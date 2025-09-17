'use client';
import { ListFilter } from 'lucide-react';
import React, { useState } from 'react';
import { QuestionInstance } from '../QuestionListWeb';

export default function Filter({
  setShowCustomOnly,
}: {
  setShowCustomOnly: React.Dispatch<React.SetStateAction<boolean>>;
}) {
  const [active, setActive] = useState(false);
  return (
    <button
      className={`mr-4  hover:rounded-md hover:bg-primary p-2 hover:text-secondary ${
        active ? 'rounded-md p-2 bg-primary text-secondary' : ''
      }`}
      onClick={() => {
        setActive((prev) => !prev);
        setShowCustomOnly((prev) => !prev);
      }}
    >
      <ListFilter className="!w-[20px] !h-[20px] " />
    </button>
  );
}
