'use client';
import React, { useState } from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';
import { usePathname } from 'next/navigation';

export default function Custom({ value }: { value?: string }) {
  const [text, setText] = useState(value ?? '');
  const pathName = usePathname();
  const hideLogo = pathName.startsWith('/question/list');
  return (
    <>
      {hideLogo ? null : (
        <div className="flex justify-center h-[70px] items-center sm:hidden">
          <Link href="/main">
            <img alt="큐메이트" width={109} height={35} className="site-logo sm:hidden" />
          </Link>
        </div>
      )}

      <div className="flex items-center justify-center h-[calc(100%-70px)] sm:h-full">
        <div className="flex flex-col h-[246px]">
          <span className="font-bold text-[24px] pb-5 text-theme-primary">
            궁금한 질문 작성하기
          </span>
          <textarea
            placeholder="내용을 입력해주세요"
            value={text}
            onChange={(e) => setText(e.target.value)}
            className="md:w-[400px] w-[305px] h-[175px] rounded-md shadow-md p-3 bg-secondary border border-gray resize-none"
          />
          <div className="pt-5 flex gap-7 justify-end ">
            <Button variant="outline" size="lg" asChild className="w-[140px]">
              <Link href="/record">취소하기</Link>
            </Button>
            {value ? (
              <Button size="lg" className="w-[140px]">
                수정하기
              </Button>
            ) : (
              <Button size="lg" className="w-[140px]">
                등록하기
              </Button>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
