import { Search } from 'lucide-react';
import React from 'react';

interface Props {
  query: string;
  setQuery: (value: string) => void;
}

export default function SearchInput({ query, setQuery }: Props) {
  return (
    <div className="flex justify-center ">
      <div className="relative w-[290px] ">
        <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-5 text-gray-500" />

        <input
          type="text"
          placeholder="질문을 검색해 주세요."
          className="border border-gray bg-secondary rounded-md text-base pl-10 py-2 w-full"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
        />
      </div>
    </div>
  );
}
