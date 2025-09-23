'use client';
import { useState } from 'react';
import OptionBtn from './OptionBtn';
interface LabelProps {
  titleLabel: string;
  option1: string;
  option2: string;
  option3: string;
  option4: string;
}

export default function RepeatSelector({
  titleLabel,
  option1,
  option2,
  option3,
  option4,
}: LabelProps) {
  const [selected, setSelected] = useState(option1);

  return (
    <>
      <div className="flex flex-col gap-2 w-full">
        <label className="text-18 text-theme-primary">{titleLabel}</label>
        <div className="flex gap-x-3 sm:gap-x-5 shadow-box p-3 w-full">
          {[option1, option2, option3, option4].map((opt) => (
            <OptionBtn
              key={opt}
              label={opt}
              active={selected === opt}
              onClick={() => setSelected(opt)}
            />
          ))}
        </div>
      </div>
    </>
  );
}
