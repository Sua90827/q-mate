import React, { useEffect, useMemo, useState } from 'react';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogFooter,
  DialogTitle,
  DialogDescription,
} from '@/components/ui/dialog';
import { Button } from '@/components/common/Button';
import PickerColumn from './PickerColumn';
import { to12h, to24h } from '@/utils/time';
import { Loader2 } from 'lucide-react';

type QuestionTimeModalProps = {
  open: boolean;
  setIsOpen: (open: boolean) => void;
  initialHour?: number;
  onSave: (hour: number) => Promise<void> | void; // returns 0~23
};

export default function QuestionTimeModal({
  open,
  setIsOpen,
  initialHour,
  onSave,
}: QuestionTimeModalProps) {
  const now = useMemo(() => new Date(), []);
  const init24 = typeof initialHour === 'number' ? initialHour : now.getHours();
  const init = to12h(init24);

  const [hour12, setHour12] = useState<number>(init.h12);
  const [meridiem, setMeridiem] = useState<'AM' | 'PM'>(init.meridiem);
  const [saving, setSaving] = useState(false);
  const [baseHour24, setBaseHour24] = useState<number>(init24);

  useEffect(() => {
    if (!open) return;
    const h24 = typeof initialHour === 'number' ? initialHour : new Date().getHours();
    const { h12, meridiem } = to12h(h24);
    setHour12(h12);
    setMeridiem(meridiem);
    setBaseHour24(h24);
  }, [open, initialHour]);

  const hours = useMemo(() => Array.from({ length: 12 }, (_, i) => i + 1), []);
  const meridiems: Array<'AM' | 'PM'> = ['AM', 'PM'];
  const isChanged = useMemo(
    () => to24h(hour12, meridiem) !== baseHour24,
    [hour12, meridiem, baseHour24],
  );

  const handleSave = async () => {
    const hour24 = to24h(hour12, meridiem);
    try {
      setSaving(true);
      await onSave(hour24);
      setIsOpen(false);
    } finally {
      setSaving(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={setIsOpen}>
      <DialogContent
        showCloseButton={false}
        className="w-[260px] z-50 rounded-2xl p-0 overflow-hidden"
        onOpenAutoFocus={(e) => e.preventDefault()}
      >
        <DialogHeader className="pt-4">
          <DialogTitle className="w-full text-center font-bold border-b pb-3">
            질문 시간 선택
          </DialogTitle>
        </DialogHeader>

        <DialogDescription asChild>
          <div className="flex items-center justify-center gap-2 py-4">
            <PickerColumn items={hours} value={hour12} onChange={(h) => setHour12(h)} />
            <span className="text-lg text-TimePicker pb-2">:</span>

            <PickerColumn items={meridiems} value={meridiem} onChange={(md) => setMeridiem(md)} />
          </div>
        </DialogDescription>

        <DialogFooter className="flex flex-row gap-0">
          <Button
            variant="outline"
            className="rounded-none flex-1 border-1 rounded-bl-2xl"
            type="button"
            onClick={() => setIsOpen(false)}
          >
            취소하기
          </Button>
          <Button
            className="rounded-none flex-1"
            type="button"
            onClick={handleSave}
            disabled={saving || !isChanged}
          >
            {saving ? (
              <span className="inline-flex items-center gap-2">
                <Loader2 className="h-4 w-4 animate-spin" />
                저장중…
              </span>
            ) : (
              '저장하기'
            )}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
