import React from 'react';
import { UseFormRegisterReturn } from 'react-hook-form';

interface TextInputProps {
  label?: string;
  type?: string;
  value?: string;
  validate?: (value: string) => boolean;
  setActive?: React.Dispatch<React.SetStateAction<boolean>>;
  onChange?: (value: string) => void;
  register?: UseFormRegisterReturn; // react-hook-form용
  placeholder?: string;
  disabled?: boolean;
}

export default function TextInput({
  label,
  type = 'text',
  value,
  validate,
  setActive,
  onChange,
  register,
  placeholder,
  disabled = false,
}: TextInputProps) {
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const newValue = e.target.value;
    onChange?.(newValue);
    if (newValue.trim() === '') {
      setActive?.(false);
      return;
    }
    setActive?.(validate ? validate(newValue) : true);
  };

  return (
    <input
      type={type}
      className={`bg-secondary rounded-md text-text-secondary w-[295px] py-2 pl-4 border border-gray ${
        disabled ? 'opacity-50 cursor-not-allowed' : ''
      }`}
      placeholder={placeholder ?? label}
      value={value?.trim()}
      onChange={register ? undefined : handleChange}
      disabled={disabled}
      {...register}
    />
  );
}
