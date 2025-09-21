import Image from 'next/image';
import React from 'react';

type BubbleyProps = {
  exp: number;
  className?: string;
};

export default function Bubbley({ exp, className }: BubbleyProps) {
  let src = '/images/bubbley/bubbley_baby.png';
  if (exp < 300) {
    src = '/images/bubbley/bubbley_baby.png';
  } else if (exp < 500) {
    src = '/images/bubbley/bubbley_child.png';
  } else {
    src = '/images/bubbley/bubbley_adult.png';
  }
  return (
    <div className={className}>
      <Image src={src} alt="버블리 캐릭터" width={120} height={167} className="opacity-90" />
    </div>
  );
}
