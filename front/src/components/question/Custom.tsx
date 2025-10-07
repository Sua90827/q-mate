'use client';
import React, { useRef, useState } from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useCreateCustomQuestion, useUpdateCustomQuestion } from '@/hooks/useCustom';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { ErrorToast } from '../common/CustomToast';
import CloseButton from '../common/CloseButton';
import TextTextarea, { TextTextareaRef } from './ui/TextTextarea';
import { useSelectedStore } from '@/store/useSelectedStore';

export default function Custom({ value }: { value?: string }) {
  const textareaRef = useRef<TextTextareaRef>(null);
  const [isEmpty, setIsEmpty] = useState<boolean>(!value?.trim());

  const pathName = usePathname();
  const startList = pathName.startsWith('/question/list');
  const customCreate = pathName.startsWith('/question/custom');
  const router = useRouter();
  const params = useSearchParams();
  const id = Number(params.get('id')?.replace('custom-', ''));
  const matchId = useMatchIdStore((state) => state.matchId);
  const setSelectedMenu = useSelectedStore((state) => state.setSelectedMenu);

  const { mutate: createCustomMutate, isPending: isCreating } = useCreateCustomQuestion();
  const { mutate: updateCustomMutate, isPending: isUpdating } = useUpdateCustomQuestion();

  const handleCreate = () => {
    const latest = textareaRef.current?.getValue() ?? '';
    if (!latest) return;
    createCustomMutate(
      { text: latest, matchId: matchId! },
      {
        onSuccess: () => router.push('/record'),
        onError: () => ErrorToast('질문이 등록되지 않았습니다. 다시 시도해 주세요.'),
      },
    );
  };

  const handleUpdate = () => {
    const latest = textareaRef.current?.getValue() ?? '';
    if (!latest) return;
    updateCustomMutate(
      { text: latest, id },
      {
        onSuccess: () => router.push('/question/list'),
        onError: () => ErrorToast('질문이 수정되지 않았습니다. 다시 시도해 주세요.'),
      },
    );
  };

  const handleTextChange = (text: string) => {
    setIsEmpty(text.length === 0);
  };

  return (
    <>
      <div className="w-full relative flex justify-center h-[70px] items-center sm:hidden">
        <Link href="/main" onClick={() => setSelectedMenu('home')}>
          <span
            className="site-logo inline-block w-[109px] h-[35px]"
            role="img"
            aria-label="큐메이트"
          />
        </Link>
        <div className="absolute right-4">
          <CloseButton
            onClick={startList ? () => router.push('/question/list') : () => router.push('/record')}
          />
        </div>
      </div>

      <div className="flex items-center justify-center h-[calc(100%-70px)] sm:h-full pb-[35px]">
        <div className="flex flex-col h-[246px]">
          <span className="font-bold text-[24px] pb-5 text-theme-primary text-center">
            궁금한 질문 작성하기
          </span>

          <TextTextarea
            ref={textareaRef}
            defaultValue={value ?? ''}
            placeholder="궁금한 질문을 입력해 보세요!"
            textLength={handleTextChange}
          />

          <div className="pt-5 flex gap-8">
            <Button variant="outline" size="lg" asChild className="md:w-[180px] w-[140px]">
              <Link href={customCreate ? '/record' : '/question/list'}>취소하기</Link>
            </Button>

            {value ? (
              <Button
                size="lg"
                className="md:w-[180px] w-[140px]"
                onClick={handleUpdate}
                disabled={isUpdating || isEmpty}
              >
                {isUpdating ? '수정 중...' : '수정하기'}
              </Button>
            ) : (
              <Button
                size="lg"
                className="md:w-[180px] w-[140px]"
                onClick={handleCreate}
                disabled={isCreating || isEmpty}
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
