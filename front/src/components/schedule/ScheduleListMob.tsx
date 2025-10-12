'use client';
import { useDeleteSchedule, useScheduleList } from '@/hooks/useSchedule';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { ErrorToast } from '../common/CustomToast';
import { Skeleton } from '../ui/skeleton';
import Loader from '../common/Loader';
import DeleteBtn from '../common/DeleteBtn';
import TrashCan from '../common/TrashCan';
import PrevBtn from '../common/PrevBtn';
import NextBtn from '../common/NextBtn';
import { useState } from 'react';
import { useRouter } from 'next/navigation';

export default function ScheduleListMob() {
  const [isDelete, setIsDelete] = useState(false);
  const router = useRouter();
  const [page, setPage] = useState<number>(0);
  const pageSize = 20;
  const matchId = useMatchIdStore((state) => state.matchId);

  const today = new Date();
  const oneYearLater = new Date(new Date().setFullYear(today.getFullYear() + 1));

  const { data, isLoading, isError } = useScheduleList(matchId!, {
    from: today.toISOString().split('T')[0],
    to: oneYearLater.toISOString().split('T')[0],
    page,
    size: pageSize,
  });

  const schedules = data?.content ?? [];
  const totalPages = data?.totalPages ?? 1;

  const {
    mutate: deleteScheduleMutate,
    isPending: deleting,
    isError: deleteError,
  } = useDeleteSchedule();

  if (deleteError) {
    ErrorToast('일정 삭제에 실패했습니다. 다시 시도해 주세요!');
  }

  if (deleting) {
    return <Loader />;
  }

  if (isLoading)
    return (
      <div className="w-full h-full">
        <div className="flex justify-center items-center h-[70px] relative">
          <p className="text-20 font-Gumi select-none">일정</p>
          <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
        </div>
        <Skeleton />
      </div>
    );

  if (isError)
    return (
      <div className="w-full h-full">
        <div className="flex justify-center items-center h-[70px] relative">
          <p className="text-20 font-Gumi select-none">일정</p>
          <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
        </div>
        <ul className="w-full border-y divide-y divide-text-gray">
          <p className="text-16">에러가 발생했습니다.</p>
        </ul>
      </div>
    );

  return (
    <div className="w-full h-full">
      <div className="flex justify-center items-center h-[70px] relative">
        <p className="text-20 font-Gumi select-none text-theme-primary">일정</p>
        <div className="absolute right-4">
          <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
        </div>
      </div>

      <div className="w-full flex-1 h-full bg-secondary ">
        <ul className="w-full border-y divide-y divide-text-gray overflow-y-auto">
          {schedules.map((list) => (
            <li key={list.eventId} className="flex justify-between px-4 py-3 items-center">
              <div className="flex-1" onClick={() => router.push(`/schedule/edit/${list.eventId}`)}>
                <span className="font-bold text-16">{list.title}</span>
                <span className="block text-text-secondary font-normal">{list.eventAt}</span>
              </div>
              {!list.isAnniversary && isDelete && (
                <DeleteBtn
                  onClick={() => deleteScheduleMutate({ matchId: matchId!, eventId: list.eventId })}
                />
              )}
            </li>
          ))}
        </ul>
      </div>

      {/* 페이지네이션 */}
      <div className="sticky bottom-[70px] flex justify-between items-center py-3 px-4 border-t border-gray bg-secondary">
        <PrevBtn page={page} setPage={setPage} />
        <div className="bg-calendar w-8 h-8 rounded-full flex justify-center items-center">
          <span>{page + 1}</span>
        </div>
        <NextBtn page={page} setPage={setPage} totalPages={totalPages} />
      </div>
    </div>
  );
}
