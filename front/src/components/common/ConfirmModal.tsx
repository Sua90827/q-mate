import { Button } from '@/components/common/Button';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import React, { Dispatch, SetStateAction } from 'react';

interface ConfirmModalProps {
  open: boolean;
  setOpen: Dispatch<SetStateAction<boolean>>;
  title: React.ReactNode;
  sub?: string;
  confirmText?: string; // 기본값 "예"
  cancelText?: string; // 기본값 "아니오"
  isDanger?: boolean; // 경고 모달 여부
  onConfirm: () => void;
  onCancel?: () => void;
}

export default function ConfirmModal({
  open,
  setOpen,
  title,
  sub,
  confirmText = '예',
  cancelText = '아니오',
  isDanger = false,
  onConfirm,
  onCancel,
}: ConfirmModalProps) {
  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogContent
        showCloseButton={false}
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] z-50"
      >
        <DialogHeader>
          <DialogTitle className="text-16 font-bold text-center leading-relaxed">
            {title}
            <span className="block text-text-secondary">{sub}</span>
          </DialogTitle>
        </DialogHeader>

        <div className="flex justify-center gap-4 py-3">
          {/* 취소 버튼 */}
          <Button
            variant={isDanger ? 'dangerOutline' : 'primaryOutline'}
            className="w-30 h-9.5"
            onClick={() => {
              setOpen(false);
              onCancel?.();
            }}
          >
            {cancelText}
          </Button>

          {/* 확인 버튼 */}
          <Button
            variant={isDanger ? 'dangerPrimary' : 'primary'}
            className={`w-30 h-9.5`}
            onClick={() => {
              setOpen(false);
              onConfirm();
            }}
          >
            {confirmText}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
