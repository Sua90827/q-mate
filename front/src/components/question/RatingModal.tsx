'use client';
import React from 'react';
import { Dialog, DialogContent, DialogDescription, DialogHeader } from '../ui/dialog';
import { Button } from '../common/Button';
import { ThumbsDownIcon, ThumbsUpIcon } from 'lucide-react';
import { DialogTitle } from '@radix-ui/react-dialog';

type Props = {
  open: boolean;
  onOpenChange?: (open: boolean) => void;
  onLike: () => void;
  onDislike: () => void;
};

export default function RatingModal({ open, onLike, onDislike, onOpenChange }: Props) {
  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent
        showCloseButton={false}
        //키보드 이벤트와 바깥 영역 클릭 방지로 평가를 하지 않으면 닫히지 않도록 설정
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] h-[160px] z-50  !pb-2 !gap-0"
      >
        <DialogHeader>
          <DialogTitle className="text-16 font-semibold text-center">
            오늘의 질문은 어땠나요?
          </DialogTitle>
          <DialogDescription className="!text-16 font-semibold text-text-primary ">
            마음에 들었다면 좋아요를 눌러주세요.
          </DialogDescription>
        </DialogHeader>

        <div className="flex justify-center gap-x-4 py-4">
          <Button variant="outline" className="w-30 h-9.5 hover:opacity-80" onClick={onDislike}>
            <ThumbsDownIcon className="!w-5 !h-5" />
          </Button>
          <Button variant="default" className="w-30 h-9.5 hover:opacity-80 " onClick={onLike}>
            <ThumbsUpIcon className="!w-5 !h-5" />
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
