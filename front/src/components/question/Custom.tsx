'use client';
import React, { useState } from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useCreateCustomQuestion, useUpdateCustomQuestion } from '@/hooks/useCustom';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { ErrorToast } from '../common/CustomToast';
import CloseButton from '../common/CloseButton';

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
    if (isCreateError) {
      ErrorToast('질문이 등록되지 않았습니다. 다시 시도해 주세요.');
    } else {
      router.push('/record');
    }
  };

  const handleUpdate = () => {
    updateCustomMutate({ text: text, id: id });
    if (isUpdateError) {
      ErrorToast('질문이 수정되지 않았습니다. 다시 시도해 주세요.');
    } else {
      router.push('/question/list');
    }
  };

  return (
    <>
      {hideLogo ? null : (
        <div className="w-full relative flex justify-center h-[70px] items-center sm:hidden">
          <Link href="/main">
            <span
              className="site-logo inline-block w-[109px] h-[35px]"
              role="img"
              aria-label="큐메이트"
            />
          </Link>
          <div className="absolute right-4">
            <CloseButton onClick={() => router.push('/record')} />
          </div>
        </div>
      )}

      <div className="flex items-center justify-center h-[calc(100%-70px)] sm:h-full">
        <div className="flex flex-col h-[246px]">
          <span className="font-bold text-[24px] pb-5 text-theme-primary">
            궁금한 질문 작성하기
          </span>
          <div className="relative">
            <textarea
              placeholder="내용을 입력해주세요"
              value={text}
              maxLength={99}
              onChange={(e) => setText(e.target.value)}
              className="md:w-[400px] w-[305px] h-[175px] rounded-md shadow-md p-3 bg-secondary border border-gray resize-none"
            />
            <span className="absolute bottom-4 right-3 text-text-secondary">{text.length}/100</span>
          </div>
          <div className="pt-5 flex gap-7 justify-end ">
            <Button variant="outline" size="lg" asChild className="w-[140px]">
              <Link href="/question/list">취소하기</Link>
            </Button>
            {value ? (
              <Button size="lg" className="w-[140px]" onClick={handleUpdate} disabled={isUpdating}>
                {isUpdating ? '수정 중...' : '수정하기'}
              </Button>
            ) : (
              <Button size="lg" className="w-[140px]" onClick={handleCreate} disabled={isCreating}>
                {isCreating ? '등록 중...' : '등록하기'}
              </Button>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
