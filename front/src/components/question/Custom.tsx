'use client';
import React, { useState } from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useCreateCustomQuestion, useUpdateCustomQuestion } from '@/hooks/useCustom';
import { useMatchIdStore } from '@/store/useMatchIdStore';

export default function Custom({ value }: { value?: string }) {
  const [text, setText] = useState(value ?? '');
  const pathName = usePathname();
  const hideLogo = pathName.startsWith('/question/list');
  const router = useRouter();
  const params = useSearchParams();
  const id = Number(params.get('id')?.replace('custom-', ''));
  const matchId = useMatchIdStore((state) => state.matchId);

  const {
    mutate: createCustomMutate,
    isPending: isCreating,
    isError: isCreateError,
  } = useCreateCustomQuestion();

  const {
    mutate: updateCustomMutate,
    isPending: isUpdating,
    isError: isUpdateError,
  } = useUpdateCustomQuestion();

  const handleCreate = () => {
    createCustomMutate({ text: text, matchId: matchId! });
    console.log('매치아이디', matchId);
    if (isCreateError) {
    } else {
      router.push('/record');
    }
  };

  const handleUpdate = () => {
    updateCustomMutate({ text: text, id: id });
    if (isUpdateError) {
    } else {
      router.push('/question/list');
    }
  };

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
              <Link href="/question/list">취소하기</Link>
            </Button>
            {value ? (
              <Button size="lg" className="w-[140px]" onClick={handleUpdate}>
                수정하기
              </Button>
            ) : (
              <Button size="lg" className="w-[140px]" onClick={handleCreate}>
                등록하기
              </Button>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
