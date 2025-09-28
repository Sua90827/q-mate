import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import React from 'react';

interface NoticeModalProps {
  open: boolean;
  setOpen: (open: boolean) => void;
  danger?: boolean;
  title: React.ReactNode; // 메인 문구
  description?: React.ReactNode; // 서브 문구
  showCloseButton?: boolean; // 닫기 버튼 표시 여부 (기본값 true)
}

export default function NoticeModal({
  open,
  setOpen,
  danger,
  title,
  description,
  showCloseButton = true,
}: NoticeModalProps) {
  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogContent
        showCloseButton={showCloseButton}
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] min-h-[153px] z-50"
      >
        <DialogHeader className="flex justify-center">
          {danger && <span className="block text-red-600">⚠️</span>}
          <DialogTitle className="text-center leading-relaxed text-16">{title}</DialogTitle>
          {description && (
            <DialogDescription className="text-14 font-semibold text-center text-text-secondary">
              {description}
            </DialogDescription>
          )}
        </DialogHeader>
      </DialogContent>
    </Dialog>
  );
}
