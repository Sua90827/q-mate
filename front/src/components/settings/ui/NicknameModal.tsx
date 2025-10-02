import { Button } from '@/components/common/Button';
import { ErrorToast } from '@/components/common/CustomToast';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { useUpdateNickname } from '@/hooks/useSetting';
import { useEffect, useState } from 'react';

interface nicknameModal {
  open: string;
  setIsOpen: React.Dispatch<React.SetStateAction<string | null>>;
  nickname: string;
  setNickname: React.Dispatch<React.SetStateAction<string>>;
}

export default function NicknameModal({ open, setIsOpen, nickname, setNickname }: nicknameModal) {
  const {
    mutate: updateNicknameMutate,
    isPending: updating,
    isError: updateError,
  } = useUpdateNickname();

  const [pendingNickname, setPendingNickname] = useState(nickname);

  const handleUpdate = () => {
    updateNicknameMutate(pendingNickname, {
      onSuccess: () => {
        setNickname(pendingNickname);
        setIsOpen(null);
      },
    });
  };

  useEffect(() => {
    if (updateError) {
      ErrorToast('닉네임 변경에 실패했습니다. 다시 시도해주세요.');
    }
  }, [updateError]);

  return (
    <Dialog open={open === 'profile'} onOpenChange={() => {}}>
      <DialogContent
        showCloseButton={false}
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] z-50"
      >
        <DialogHeader>
          <DialogTitle className="text-center leading-relaxed">
            <input
              type="text"
              placeholder="변경할 닉네임을 입력해주세요."
              className="rounded-md border border-dash pl-4 py-2"
              value={pendingNickname}
              onChange={(e) => setPendingNickname(e.target.value)}
            />
          </DialogTitle>
        </DialogHeader>

        <div className="flex justify-center gap-4 py-3">
          <Button
            variant="outline"
            className="w-30 h-9.5 hover:opacity-80"
            onClick={() => setIsOpen(null)}
          >
            취소하기
          </Button>
          <Button
            variant="default"
            className="w-30 h-9.5 hover:opacity-80"
            onClick={handleUpdate}
            disabled={updating}
          >
            {updating ? '변경 중...' : '변경하기'}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
