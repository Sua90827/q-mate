import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import React from 'react';

export default function InviteCopyErrorModal({
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
        className="w-[285px] z-50"
      >
        <DialogHeader>
          <DialogTitle className="text-center leading-relaxed">
            <p> 복사에 실패했어요.</p>
            <p>다시 시도해주세요!</p>
          </DialogTitle>
        </DialogHeader>
      </DialogContent>
    </Dialog>
  );
}
