import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import React from 'react';
import { Button } from './Button';

interface NoticeModalProps {
  open: boolean;
  setOpen: (open: boolean) => void;
  danger?: boolean;
  title: React.ReactNode; // 메인 문구
  sub?: React.ReactNode; // 서브 문구
  showCloseButton?: boolean; // 닫기 버튼 표시 여부 (기본값 true)
}

interface NoticeModalProps {
  open: boolean;
  setOpen: (open: boolean) => void;
  danger?: boolean;
  title: React.ReactNode;
  sub?: React.ReactNode;
  showCloseButton?: boolean;
  onConfirm?: () => void;
}

export default function NoticeModal({
  open,
  setOpen,
  danger,
  title,
  sub,
  showCloseButton = false,
  onConfirm,
}: NoticeModalProps) {
  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogContent
        showCloseButton={showCloseButton}
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] min-h-[153px] z-50"
      >
        <DialogHeader className="flex justify-center pt-[10.5px]">
          <DialogTitle className="text-center leading-relaxed text-16">{title}</DialogTitle>
          {sub && (
            <DialogDescription className="text-16 font-semibold text-center text-text-secondary">
              {sub}
            </DialogDescription>
          )}
          <div className="flex justify-center gap-4 py-3">
            <Button
              variant={danger ? 'dangerPrimary' : 'primary'}
              className="w-full h-9.5"
              onClick={() => {
                setOpen(false);
                onConfirm?.();
              }}
            >
              확인
            </Button>
          </div>
        </DialogHeader>
      </DialogContent>
    </Dialog>
  );
}
