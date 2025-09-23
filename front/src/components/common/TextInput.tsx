import React, { useState } from 'react';

interface TextInputProps {
  label?: string;
  type?: string;
  validate?: (value: string) => boolean;
  setActive: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function TextInput({ label, type = 'text', validate, setActive }: TextInputProps) {
  const [inputValue, setInputValue] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setInputValue(value);

    if (value.trim() === '') {
      setActive(false);
      return;
    }

    setActive(validate ? validate(value) : true);
  };

  return (
    <input
      type={type}
      className="bg-secondary rounded-md text-text-secondary w-[295px] py-2 pl-4 border border-gray"
      placeholder={label}
      value={inputValue}
      onChange={handleChange}
    />
  );
}
