'use client';
import React, { useState, forwardRef, useImperativeHandle, ChangeEvent } from 'react';

export interface TextTextareaRef {
  getValue: () => string;
}

export interface TextTextareaProps {
  defaultValue?: string;
  placeholder?: string;
  textLength?: (text: string) => void; // 부모에 길이 전달용
}

const TextTextarea = forwardRef<TextTextareaRef, TextTextareaProps>(
  ({ defaultValue = '', placeholder, textLength }, ref) => {
    const [text, setText] = useState(defaultValue);
    const [hasEdited, setHasEdited] = useState(false);

    const isBlank = text.trim().length === 0;
    const showWarning = hasEdited && isBlank;

    useImperativeHandle(ref, () => ({
      getValue: () => text.trim(),
    }));

    const handleChange = (e: ChangeEvent<HTMLTextAreaElement>) => {
      const value = e.target.value;
      setText(value);
      if (!hasEdited) setHasEdited(true);
      textLength?.(value.trim()); // 콜백
    };

    return (
      <div className="relative md:w-[390px] w-[310px] h-[140px]">
        <textarea
          placeholder={hasEdited ? '내용을 입력해주세요.' : placeholder}
          value={text}
          maxLength={100}
          onChange={handleChange}
          className={`w-full h-full rounded-md shadow-md p-3 resize-none border text-14 ${
            showWarning
              ? 'border-gray bg-secondary placeholder-red-400'
              : 'border-gray bg-secondary'
          }`}
        />
        <span className="absolute bottom-4 right-3 text-dash">{text.length}/100</span>
      </div>
    );
  },
);

TextTextarea.displayName = 'TextTextarea';
export default TextTextarea;
