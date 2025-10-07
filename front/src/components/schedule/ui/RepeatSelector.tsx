'use client';
import { useState } from 'react';
import OptionBtn from './OptionBtn';

interface LabelProps {
  titleLabel: string;
  options: { label: string; value: string }[]; // label은 한글, value는 서버용
  value?: string; // 부모가 제어할 때
  defaultValue?: string;
  onChange?: (value: string) => void;
}

export default function RepeatSelector({
  titleLabel,
  options,
  value,
  defaultValue,
  onChange,
}: LabelProps) {
  const [selected, setSelected] = useState<string>(defaultValue ?? options[0].value);
  const current = value ?? selected;
  const handleClick = (value: string) => {
    setSelected(value);
    onChange?.(value); // 부모에 서버용 value 전달
  };

  return (
    <div className="flex flex-col gap-2 w-full">
      <label className="text-18 text-theme-primary">{titleLabel}</label>
      <div className="flex gap-x-3 md:gap-x-5 shadow-box p-3 w-full">
        {options.map((opt) => (
          <OptionBtn
            key={opt.value}
            label={opt.label}
            active={current === opt.value}
            onClick={() => handleClick(opt.value)}
          />
        ))}
      </div>
    </div>
  );
}
