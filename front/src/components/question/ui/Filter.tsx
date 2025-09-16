import { ListFilter } from 'lucide-react';
import React from 'react';

export default function Filter() {
  return (
    <div className="flex justify-between items-center">
      <span className="inline-block text-20 font-bold py-4 pl-4 cursor-none">질문 리스트</span>
      <ListFilter className="mr-4 !w-[20px]" />
    </div>
  );
}
