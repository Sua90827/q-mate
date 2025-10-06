import { Button } from '@/components/common/Button';
import { ErrorToast, SuccessToast } from '@/components/common/CustomToast';
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
    updateNicknameMutate(
      { nickname: pendingNickname },
      {
        onSuccess: () => {
          setNickname(pendingNickname);
          setIsOpen(null);
          SuccessToast('닉네임이 성공적으로 변경되었습니다.');
        },
      },
    );
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
        className="w-[285px] z-50 pt-[31px]"
      >
        <DialogHeader>
          <DialogTitle className="text-center leading-relaxed">
            <input
              type="text"
              placeholder="변경할 닉네임을 입력해주세요."
              className="rounded-md border border-dash pl-4 py-2"
              maxLength={10}
              value={pendingNickname}
              onChange={(e) => setPendingNickname(e.target.value)}
            />
            {pendingNickname.length > 0 && pendingNickname.length < 2 && (
              <p className="text-sm text-start text-red-500 pl-3 mt-2">
                닉네임은 최소 2자 이상이어야 합니다.
              </p>
            )}
            {/^[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]/.test(pendingNickname) && (
              <p className="text-sm text-start text-red-500 pl-3 mt-2">
                닉네임은 특수문자로 시작할 수 없습니다.
              </p>
            )}
          </DialogTitle>
        </DialogHeader>

        <div className="flex justify-center gap-x-4 py-3">
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
            disabled={
              updating ||
              pendingNickname.length < 2 ||
              /^[^a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]/.test(pendingNickname)
            }
          >
            {updating ? '변경 중...' : '변경하기'}
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
