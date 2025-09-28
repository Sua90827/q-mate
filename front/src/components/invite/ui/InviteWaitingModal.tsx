import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import React from 'react';

export default function InviteWaitingModal({
  open,
  setOpen,
}: {
  open: boolean;
  setOpen: (open: boolean) => void;
}) {
  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogContent
        showCloseButton={true}
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] h-[153px] z-50"
      >
        <DialogHeader className="flex justify-center">
          <DialogTitle className="text-center leading-relaxed text-16">
            <p>상대방을 기다리는 중이에요. 🐢</p>
          </DialogTitle>
          <DialogDescription className="text-14 font-semibold text-center text-text-secondary">
            <p> 상대방이 초대 코드를 입력하면</p>
            <p>자동으로 연결돼요.</p>
          </DialogDescription>
        </DialogHeader>
      </DialogContent>
    </Dialog>
  );
}
