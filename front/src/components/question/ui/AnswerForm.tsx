'use client';
import React, { useCallback, useMemo, useState } from 'react';
import { usePathname, useRouter } from 'next/navigation';
import CloseButton from '@/components/common/CloseButton';
import { Button } from '@/components/common/Button';
import { Loader2 } from 'lucide-react';
import RatingModal from '../RatingModal';
import Loader from '@/components/common/Loader';
import Link from 'next/link';
import TextTextarea from './TextTextarea';

type AnswerFormProps = {
  mode: 'create' | 'edit';
  questionText: string;
  onSubmit: (content: string) => Promise<void> | void;
  submitting?: boolean;
  initialValue?: string;
};

export default function AnswerForm({
  questionText,
  mode,
  onSubmit,
  submitting = false,
  initialValue = '',
}: AnswerFormProps) {
  const router = useRouter();
  const [content, setContent] = useState<string>(initialValue);
  const [open, setOpen] = useState(false);
  const pathName = usePathname();
  const fromToday = pathName.startsWith('/question/detail');
  const isBlank = content.trim().length === 0;
  const canSubmit = !submitting && !isBlank;

  const handleCommit = useCallback((text: string) => {
    setContent(text);
  }, []);

  const handleSubmit = async () => {
    if (!canSubmit) return;
    await onSubmit(content);
    if (mode === 'create') {
      setOpen(true);
    }
    router.push('/record');
    <Loader />;
  };

  return (
    <>
      {/* 상단 닫기 버튼 (모바일 헤더) */}
      <div className="w-full relative top-0 h-[70px] flex justify-center items-center sm:hidden">
        <span aria-label="큐메이트" className="site-logo w-[109px] h-35px" />
        <div className="absolute top-5 right-5 sm:hidden">
          <CloseButton onClick={() => router.push('/question/list')} />
        </div>
      </div>

      <div className="flex flex-col items-center justify-center h-full bg-gradient-sub ">
        <div className="flex flex-col h-[246px] gap-3">
          <span className="font-bold text-24 text-center pb-3 text-theme-primary">
            {questionText}
          </span>

          <TextTextarea
            defaultValue={content}
            placeholder="궁금한 질문을 입력해 보세요!"
            onCommit={handleCommit}
          />
        </div>

        <div className="pt-5 flex gap-7">
          <Button variant="outline" size="lg" asChild className="md:w-[180px] w-[140px]">
            <Link href={fromToday ? '/record' : '/question/list'}>취소하기</Link>
          </Button>

          <Button
            size="lg"
            className="md:w-[180px] w-[140px]"
            onClick={handleSubmit}
            disabled={!canSubmit}
            aria-busy={submitting}
          >
            {submitting ? (
              <Loader2 className="w-4 h-4 animate-spin" />
            ) : mode === 'create' ? (
              '답변하기'
            ) : (
              '수정하기'
            )}
          </Button>
        </div>
      </div>
      <RatingModal
        open={open}
        onOpenChange={setOpen}
        onLike={() => router.push('/question/list')}
        onDislike={() => router.push('/question/list')}
      />
    </>
  );
}
