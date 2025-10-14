import React, { useEffect, useState } from 'react';
import { Dialog, DialogContent } from '../ui/dialog';
import { useFetchChart } from '@/hooks/useChart';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { Chart } from './Chart';

export default function ChartModal() {
  const [open, setOpen] = useState(false);
  const matchId = useMatchIdStore((state) => state.matchId);
  const { data } = useFetchChart(matchId!);

  useEffect(() => {
    if (!data) return;

    const today = new Date().getDate();
    const month = new Date().getMonth();
    const seeMonth = localStorage.getItem('chartModal');

    // 좋아요 데이터가 존재하고, 이번 달에 아직 안봤을 때만 모달 오픈
    const hasLikes = data.totalLikes > 0;

    if (today === 1 && Number(seeMonth) !== month && hasLikes) {
      setOpen(true);
      localStorage.setItem('chartModal', String(month));
    }
  }, [data]);

  if (!data) return null;

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogContent
        onEscapeKeyDown={(e) => e.preventDefault()}
        onPointerDownOutside={(e) => e.preventDefault()}
        className="w-[300px] max-h-[450px] z-50 !gap-0 !pb-0 !mb-0 flex flex-col justify-center"
      >
        <Chart data={data} />
      </DialogContent>
    </Dialog>
  );
}
