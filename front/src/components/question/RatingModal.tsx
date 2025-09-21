'use client';
import React from 'react';
import { Dialog, DialogContent, DialogDescription, DialogHeader } from '../ui/dialog';
import { Button } from '../common/Button';
import { ThumbsDownIcon, ThumbsUpIcon } from 'lucide-react';
import { DialogTitle } from '@radix-ui/react-dialog';
import { useThemeStore } from '@/store/useThemeStore';

type Props = {
  open: boolean;
  // props로 넘겨주는 액션 전송api에사용
  onLike: () => void;
  onDislike: () => void;
};

export default function RatingModal({ open, onLike, onDislike }: Props) {
  const theme = useThemeStore((state) => state.theme);

  return (
    <Dialog open={open} onOpenChange={() => {}}>
      <DialogContent
        showCloseButton={false}
        //키보드 이벤트와 바깥 영역 클릭 방지로 평가를 하지 않으면 닫히지 않도록 설정
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] h-[160px] z-50"
      >
        <DialogHeader>
          <DialogTitle className="text-[14px] font-semibold">오늘의 질문은 어땠나요?</DialogTitle>
          <DialogDescription className="text-[14px] font-semibold text-text-primary">
            마음에 들었다면 좋아요를 눌러주세요.
          </DialogDescription>
        </DialogHeader>

        <div className="flex justify-center gap-4 py-4">
          <Button
            variant="outline"
            className="w-30 h-9.5 hover:opacity-80"
            onClick={onDislike}
            theme={theme}
          >
            <ThumbsDownIcon className="w-6 h-6" />
          </Button>
          <Button
            variant="default"
            className="w-30 h-9.5 hover:opacity-80 "
            onClick={onLike}
            theme={theme}
          >
            <ThumbsUpIcon className="w-6 h-6" />
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
