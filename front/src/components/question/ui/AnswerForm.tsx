'use client';
import React, { useMemo, useState } from 'react';
import { useRouter } from 'next/navigation';
import CloseButton from '@/components/common/CloseButton';
import { Button } from '@/components/common/Button';
import { Loader2 } from 'lucide-react';
import RatingModal from '../RatingModal';
import Loader from '@/components/common/Loader';

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

  const length = content.length;
  const tooLong = length > 100;
  const isBlank = content.trim().length === 0;
  const canSubmit = !submitting && !tooLong && !isBlank;

  const helperText = useMemo(() => {
    if (tooLong) return '답변은 최대 100자까지 가능합니다.';
    if (isBlank)
      return mode === 'create' ? '답변을 입력해 주세요.' : '수정할 내용을 입력해 주세요.';
    return '';
  }, [tooLong, isBlank, mode]);

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

      <div className="flex flex-col items-center justify-center h-full bg-gradient-sub gap-10">
        <div className="flex flex-col h-[246px] gap-3">
          <span className="font-bold text-24 text-center pb-3 text-theme-primary">
            {questionText}
          </span>
          <div className="relative md:w-[400px] w-[310px] h-[175px] rounded-md shadow-md p-3 bg-secondary border border-gray text-[14px] outline-none">
            <textarea
              value={content}
              onChange={(e) => setContent(e.target.value)}
              placeholder="오늘의 질문에 답변을 해보세요! (최대 100자)"
              className="w-full h-full resize-none outline-none"
              maxLength={100}
            />

            {/* 길이/에러 안내 및 글자수 카운터 */}

            <p
              className={`text-12 absolute bottom-5 left-3 ${
                helperText ? 'text-red-500' : 'text-dash'
              }`}
            >
              {helperText || <>&nbsp;</>}
            </p>
            <span
              className={`${
                tooLong ? 'text-red-500' : 'text-dash'
              } text-12 absolute bottom-5 right-3`}
            >
              {length}/100
            </span>
          </div>

          <div className="w-full flex justify-center md:justify-end mt-2">
            <Button
              size="lg"
              className="w-[310px] sm:w-[200px]"
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
