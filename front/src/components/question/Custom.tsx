'use client';
import React, { useState, useCallback } from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useCreateCustomQuestion, useUpdateCustomQuestion } from '@/hooks/useCustom';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { ErrorToast } from '../common/CustomToast';
import CloseButton from '../common/CloseButton';
import TextTextarea from './ui/TextTextarea';

export default function Custom({ value }: { value?: string }) {
  const [finalText, setFinalText] = useState(value ?? '');
  const pathName = usePathname();
  const hideLogo = pathName.startsWith('/question/list');
  const customCreate = pathName.startsWith('/question/custom');
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

  const handleCommit = useCallback((text: string) => {
    setFinalText(text);
  }, []);

  const handleCreate = () => {
    if (!finalText.trim()) return;
    createCustomMutate({ text: finalText, matchId: matchId! });
    if (isCreateError) {
      ErrorToast('질문이 등록되지 않았습니다. 다시 시도해 주세요.');
    } else {
      router.push('/record');
    }
  };

  const handleUpdate = () => {
    if (!finalText.trim()) return;
    updateCustomMutate({ text: finalText, id });
    if (isUpdateError) {
      ErrorToast('질문이 수정되지 않았습니다. 다시 시도해 주세요.');
    } else {
      router.push('/question/list');
    }
  };

  return (
    <>
      {!hideLogo && (
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

      <div className="flex items-center justify-center h-[calc(100%-70px)] sm:h-full pb-[70px]">
        <div className="flex flex-col h-[246px]">
          <span className="font-bold text-[24px] pb-5 text-theme-primary text-center">
            궁금한 질문 작성하기
          </span>

          <TextTextarea
            defaultValue={value ?? ''}
            placeholder="궁금한 질문을 입력해 보세요!"
            onCommit={handleCommit}
          />

          <div className="pt-5 flex gap-7">
            <Button variant="outline" size="lg" asChild className="md:w-[180px] w-[140px]">
              <Link href={customCreate ? '/record' : '/question/list'}>취소하기</Link>
            </Button>

            {value ? (
              <Button
                size="lg"
                className="md:w-[180px] w-[140px]"
                onClick={handleUpdate}
                disabled={isUpdating || !finalText.trim()}
              >
                {isUpdating ? '수정 중...' : '수정하기'}
              </Button>
            ) : (
              <Button
                size="lg"
                className="md:w-[180px] w-[140px]"
                onClick={handleCreate}
                disabled={isCreating || !finalText.trim()}
              >
                {isCreating ? '등록 중...' : '등록하기'}
              </Button>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
