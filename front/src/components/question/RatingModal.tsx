'use client';
import React from 'react';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { Button } from '../common/Button';
import { ThumbsDownIcon, ThumbsUpIcon } from 'lucide-react';

type Props = {
  open: boolean; // 모달 열림 상태는 value값이 null일 경우
  onLike: () => void; // 부모가 넘겨주는 액션 (API 호출 등)
  onDislike: () => void;
};

export default function RatingModal({ open }: Props) {
  return (
    <Dialog open={true} onOpenChange={() => {}}>
      <DialogContent
        showCloseButton={false}
        //키보드 이벤트와 바깥 영역 클릭 방지로 평가를 하지 않으면 닫히지 않도록 설정
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] h-[160px]"
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
            onClick={() => {
              /* 추가필요 */
            }}
          >
            <ThumbsDownIcon className="w-6 h-6" />
          </Button>
          <Button
            variant="default"
            className="w-30 h-9.5 hover:opacity-80 "
            onClick={() => {
              /* 추가필요 */
            }}
          >
            <ThumbsUpIcon className="w-6 h-6" />
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
