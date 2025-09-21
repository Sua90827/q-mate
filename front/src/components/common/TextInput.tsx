import React, { useState } from 'react';

interface TextInputProps {
  label: string;
  type: string;
  setActive: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function TextInput({ label, type = 'text', setActive }: TextInputProps) {
  const [inputValue, setInputValue] = useState('');

  return (
    <input
      type={type}
      className="bg-secondary rounded-md text-text-secondary w-[295px] py-2 pl-4 border border-gray"
      placeholder={label}
      value={inputValue}
      onChange={(e) => {
        const value = e.target.value;
        setInputValue(value);

        if (value.trim() === '') {
          setActive(false);
        } else {
          {
            /*비밀번호는 문자,숫자,특수문자 포함 8자리 이상*/
          }
          if (
            type === 'password' &&
            value.length >= 8 &&
            /[0-9]/.test(value) &&
            /[a-zA-Z]/.test(value) &&
            /[^a-zA-Z0-9]/.test(value)
          ) {
            setActive(true);
          }
        }
      }}
    />
  );
}
