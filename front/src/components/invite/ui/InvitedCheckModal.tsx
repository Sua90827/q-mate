import { fetchInvitedCheck } from '@/api/invite';
import { Button } from '@/components/common/Button';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { InvitedCheck } from '@/types/InviteType';
import React, { useEffect, useState } from 'react';

interface InvitedCheckModal {
  open: boolean;
  setIsOpen: React.Dispatch<React.SetStateAction<boolean>>;
}

export default function InvitedCheckModal({ open, setIsOpen }: InvitedCheckModal) {
  const [invited, setInvited] = useState<InvitedCheck | null>(null);

  useEffect(() => {
    const load = async () => {
      const res = await fetchInvitedCheck();
      setInvited(res);
    };
    if (open) load();
  }, [open]);

  return (
    <Dialog open={open} onOpenChange={() => {}}>
      <DialogContent
        showCloseButton={false}
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] z-50"
      >
        <DialogHeader>
          <DialogTitle className="text-16 font-bold text-center leading-relaxed">
            <p>{invited?.partnerNickname}님과 함께</p>
            <p>이야기를 기록하시겠습니까?</p>
          </DialogTitle>
        </DialogHeader>

        <div className="flex justify-center gap-4 py-3">
          <Button
            variant="primaryOutline"
            className="w-30 h-9.5 hover:opacity-80"
            onClick={() => setIsOpen(false)}
          >
            예
          </Button>
          <Button
            variant="primary"
            className="w-30 h-9.5 hover:opacity-80 "
            onClick={() => setIsOpen(false)}
          >
            아니오
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
