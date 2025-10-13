'use client';

import { Button } from '@/components/common/Button';
import { Share2 } from 'lucide-react';
import React, { useState, useRef } from 'react';
import ConfirmModal from '@/components/common/ConfirmModal';

type Props = {
  targetRef?: React.RefObject<HTMLElement | null>;
  targetId?: string;
  title?: string;
  text?: string;
  className?: string;
};

export default function ShareBtn({ targetId, title, text, className, targetRef }: Props) {
  const [open, setOpen] = useState(false);
  const blobRef = useRef<Blob | null>(null);

  const handleShare = async () => {
    const el: HTMLElement | null =
      (targetRef?.current as HTMLElement | null) ??
      (targetId ? document.getElementById(targetId) : null);

    if (!el) {
      console.log(
        `대상 요소를 찾을 수 없습니다. ref=${!!targetRef?.current}, id=${targetId ?? '(none)'}`,
      );
      return;
    }

    const nav = navigator as Navigator & { canShare?: (data?: ShareData) => boolean };

    let blob: Blob | null = null;
    try {
      const { toBlob } = await import('html-to-image');
      blob = await toBlob(el, {
        cacheBust: true,
        pixelRatio: 2,
        style: {
          backgroundImage: 'linear-gradient(to bottom, #f2faff 40%, #d4eeff 100%)',
        },
      });
    } catch (e) {
      console.log('image 변환 실패', e);
    }

    if (blob && typeof navigator !== 'undefined' && 'share' in navigator) {
      const file = new File([blob], 'answer.png', { type: 'image/png' });
      const canShareFiles = 'canShare' in navigator && nav.canShare?.({ files: [file] });
      try {
        if (canShareFiles) {
          await nav.share({ files: [file], title, text });
          return;
        }
      } catch (error) {
        console.log('파일 공유 실패', error);
      }
    }

    if (blob) {
      blobRef.current = blob;
      setOpen(true);
      return;
    }

    alert('공유를 지원하지 않는 환경입니다.');
  };

  const doDownload = () => {
    if (!blobRef.current) return;
    const url = URL.createObjectURL(blobRef.current);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'answer.png';
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
    setOpen(false);
  };

  return (
    <>
      <Button
        type="button"
        onClick={handleShare}
        aria-label="공유"
        className={`w-16 h-16 rounded-full flex items-center justify-center absolute bottom-4 right-4 cursor-pointer ${className}`}
      >
        <Share2 className="text-secondary" />
      </Button>
      <ConfirmModal
        defaultStyle
        open={open}
        setOpen={setOpen}
        title="이미지로 저장할까요?"
        confirmText="확인"
        cancelText="취소"
        onConfirm={doDownload}
      />
    </>
  );
}
