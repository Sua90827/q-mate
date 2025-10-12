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

  if (deleting) return <Loader />;

  if (isLoading)
    return (
      <div className="fixed inset-0 w-full h-dvh md:h-screen overflow-hidden flex flex-col pt-[env(safe-area-inset-top)] pb-[env(safe-area-inset-bottom)] touch-pan-y">
        <div className="flex justify-center items-center h-[70px] relative flex-shrink-0 bg-secondary">
          <p className="text-20 font-Gumi select-none text-theme-primary">일정</p>
          <div className="absolute right-4">
            <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
          </div>
        </div>
        <div className="flex-1 overflow-y-auto overscroll-contain bg-secondary">
          <Skeleton />
        </div>
        <div className="flex-shrink-0 h-[60px] flex justify-between items-center py-3 px-4 border-t border-gray bg-secondary">
          <PrevBtn page={page} setPage={setPage} />
          <div className="bg-calendar w-8 h-8 rounded-full flex justify-center items-center">
            <span>{page + 1}</span>
          </div>
          <NextBtn page={page} setPage={setPage} totalPages={totalPages} />
        </div>
      </div>
    );

  if (isError)
    return (
      <div className="fixed inset-0 w-full h-dvh md:h-screen overflow-hidden flex flex-col pt-[env(safe-area-inset-top)] pb-[env(safe-area-inset-bottom)] touch-pan-y">
        <div className="flex justify-center items-center h-[70px] relative flex-shrink-0 bg-secondary">
          <p className="text-20 font-Gumi select-none text-theme-primary">일정</p>
          <div className="absolute right-4">
            <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
          </div>
        </div>
        <div className="flex-1 overflow-y-auto overscroll-contain bg-secondary">
          <p className="text-16 p-4">에러가 발생했습니다.</p>
        </div>
        <div className="flex-shrink-0 h-[60px] flex justify-between items-center py-3 px-4 border-t border-gray bg-secondary">
          <PrevBtn page={page} setPage={setPage} />
          <div className="bg-calendar w-8 h-8 rounded-full flex justify-center items-center">
            <span>{page + 1}</span>
          </div>
          <NextBtn page={page} setPage={setPage} totalPages={totalPages} />
        </div>
      </div>
    );

  return (
    <div className="fixed inset-0 w-full h-dvh md:h-screen overflow-hidden flex flex-col pt-[env(safe-area-inset-top)] pb-[env(safe-area-inset-bottom)] touch-pan-y">
      {/* 헤더 */}
      <div className="flex justify-center items-center h-[70px] relative flex-shrink-0 ">
        <p className="text-20 font-Gumi select-none text-theme-primary">일정</p>
        <div className="absolute right-4">
          <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
        </div>
      </div>

      {/* 스크롤 영역*/}
      <div className="flex-1 overflow-y-auto overscroll-contain bg-secondary">
        <ul className="w-full border-y divide-y divide-text-gray">
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
      <div className="flex-shrink-0 sticky bottom-[70px] pb-[env(safe-area-inset-bottom)] flex justify-between items-center py-3 px-4 border-t border-gray bg-secondary">
        <PrevBtn page={page} setPage={setPage} />
        <div className="bg-calendar w-8 h-8 rounded-full flex justify-center items-center">
          <span>{page + 1}</span>
        </div>
        <NextBtn page={page} setPage={setPage} totalPages={totalPages} />
      </div>
    </div>
  );
}
