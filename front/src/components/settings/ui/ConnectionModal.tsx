import { Button } from '@/components/common/Button';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';
import { cn } from '@/lib/utils';
import { Loader2 } from 'lucide-react';
import React from 'react';

type Props = {
  open: boolean;
  setIsOpen: (open: boolean) => void;
  onClick: () => Promise<void> | void; // returns 0~23
  status: 'ACTIVE' | 'WAITING';
  loading: boolean;
};
export default function ConnectionModal({ open, setIsOpen, onClick, status, loading }: Props) {
  return (
    <Dialog open={open} onOpenChange={setIsOpen}>
      <DialogContent
        showCloseButton={false}
        className="w-[285px] h-[165px] z-50 rounded-lg p-0 overflow-hidden gap-0"
        onOpenAutoFocus={(e) => e.preventDefault()}
      >
        <DialogHeader className={cn(status === 'ACTIVE' ? 'pt-7 px-4' : 'pt-12 px-4')}>
          <DialogTitle className="w-full text-center font-14 font-semibold whitespace-pre-line leading-6">
            {status === 'ACTIVE'
              ? '지금까지의 이야기를 마무리하고\n연결을 해제 하시겠습니까?'
              : '끊어진 연결을 복구하고\n이야기를 계속할 수 있어요.'}
          </DialogTitle>
        </DialogHeader>

        {status === 'ACTIVE' && (
          <DialogDescription className="text-center font-regular !text-14 py-1 text-text-secondary ">
            2주 동안은 언제든 다시 복구할 수 있어요.
          </DialogDescription>
        )}

        <DialogFooter className="w-full flex !flex-row align-center !justify-center gap-5 pb-[15px]">
          <Button
            className="w-[110px] rounded-xl"
            variant="outline"
            type="button"
            onClick={() => setIsOpen(false)}
          >
            취소하기
          </Button>
          <Button className="w-[110px] rounded-xl" type="button" onClick={onClick}>
            {loading ? (
              <span className="inline-flex items-center gap-2">
                <Loader2 className="h-4 w-4 animate-spin" />
                {status === 'ACTIVE' ? '연결 중...' : '복구 중...'}
              </span>
            ) : status === 'ACTIVE' ? (
              '연결 끊기'
            ) : (
              '복구하기'
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
