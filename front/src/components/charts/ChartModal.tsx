import React, { useEffect, useState } from 'react';
import { Dialog, DialogContent, DialogDescription, DialogHeader, DialogTitle } from '../ui/dialog';
import { Chart } from './Chart';

export default function ChartModal() {
  const [open, setOpen] = useState(false);

  useEffect(() => {
    const today = new Date().getDate();
    const month = new Date().getMonth();

    const seeMonth = localStorage.getItem('chartModal');

    if (today === 2 && Number(seeMonth) !== month) {
      setOpen(true);
      localStorage.setItem('chartModal', String(month));
    }
  }, []);
  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogContent
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[300px] h-[450px] z-50 !gap-0 !pb-0 !mb-0"
      >
        <Chart />
      </DialogContent>
    </Dialog>
  );
}
