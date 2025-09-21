import { Button } from '@/components/common/Button';
import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { useThemeStore } from '@/store/useThemeStore';

interface nicknameModal {
  open: string;
  setIsOpen: React.Dispatch<React.SetStateAction<string | null>>;
}

export default function NicknameModal({ open, setIsOpen }: nicknameModal) {
  const theme = useThemeStore((state) => state.theme);

  return (
    <Dialog open={open === 'profile' ? true : false} onOpenChange={() => {}}>
      <DialogContent
        showCloseButton={false}
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] z-50"
      >
        <DialogHeader>
          <DialogTitle className="  text-center leading-relaxed">
            <input
              type="text"
              placeholder="변경할 닉네임을 입력해주세요."
              className="rounded-md border border-dash pl-4 py-2"
            />
          </DialogTitle>
        </DialogHeader>

        <div className="flex justify-center gap-4 py-3">
          <Button
            variant="outline"
            theme={theme}
            className="w-30 h-9.5 hover:opacity-80"
            onClick={() => setIsOpen(null)}
          >
            취소하기
          </Button>
          <Button
            variant="default"
            theme={theme}
            className="w-30 h-9.5 hover:opacity-80 "
            onClick={() => setIsOpen(null)}
          >
            변경하기
          </Button>
        </div>
      </DialogContent>
    </Dialog>
  );
}
