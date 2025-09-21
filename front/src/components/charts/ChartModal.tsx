import React, { useEffect, useState } from 'react';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '../ui/dialog';
import { Button } from '../common/Button';
import { Chart } from './Chart';

export default function ChartModal() {
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const today = new Date().getDate();
    const month = new Date().getMonth();

    const seeMonth = localStorage.getItem('chartModal');

    if (today === 21 && Number(seeMonth) !== month) {
      setOpen(true);
      localStorage.setItem('chartModal', String(month));
    }
  }, []);
  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogContent
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[285px] h-[350px] z-50 gap-0"
      >
        <DialogHeader className="pt-8">
          <DialogTitle className="text-16 font-semibold text-center">
            큐메이트와 함께 한 저번달
          </DialogTitle>
          <DialogDescription className="!text-16 font-semibold text-center text-text-primary">
            좋아해주신 질문들을 분석해 봤어요!
          </DialogDescription>
        </DialogHeader>
        <Chart />
      </DialogContent>
    </Dialog>
  );
}
