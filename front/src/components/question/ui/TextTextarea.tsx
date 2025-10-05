'use client';
import React, { useState } from 'react';

type TextTextareaProps = {
  defaultValue?: string;
  placeholder?: string;
  onCommit: (text: string) => void;
};

export default function TextTextarea({
  defaultValue = '',
  placeholder,
  onCommit,
}: TextTextareaProps) {
  const [text, setText] = useState(defaultValue);
  const [hasEdited, setHasEdited] = useState(false);

  const isBlank = text.trim().length === 0;
  const showWarning = hasEdited && isBlank;

  const handleChange = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setText(e.target.value);
    if (!hasEdited) setHasEdited(true);
  };

  const handleBlur = () => {
    onCommit(text.trim());
  };

  return (
    <div className="relative md:w-[390px] w-[310px] h-[180px]">
      <textarea
        placeholder={hasEdited ? '내용을 입력해주세요.' : placeholder}
        value={text}
        maxLength={100}
        onChange={handleChange}
        onBlur={handleBlur}
        className={`w-full h-full rounded-md shadow-md p-3 resize-none border ${
          showWarning ? 'border-gray bg-secondary placeholder-red-400' : 'border-gray bg-secondary'
        }`}
      />
      <span className="absolute bottom-4 right-3 text-text-secondary">{text.length}/100</span>
    </div>
  );
}
