'use client';
import { useDeleteSchedule, useScheduleList } from '@/hooks/useSchedule';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { Skeleton } from '../ui/skeleton';
import DeleteBtn from '../common/DeleteBtn';
import PrevBtn from '../common/PrevBtn';
import NextBtn from '../common/NextBtn';

import { useState } from 'react';
import { useRouter } from 'next/navigation';
import { ErrorToast } from '../common/CustomToast';

export default function ScheduleListWeb() {
  const matchId = useMatchIdStore((state) => state.matchId);
  const [page, setPage] = useState<number>(0);
  const router = useRouter();
  const pageSize = 20;

  const today = new Date();
  const oneYearLater = new Date(new Date().setFullYear(today.getFullYear() + 1));

  const { data, isLoading, isError } = useScheduleList(matchId!, {
    from: today.toISOString().split('T')[0],
    to: oneYearLater.toISOString().split('T')[0],
    page: page,
    size: pageSize,
  });

  const schedules = data?.content ?? [];
  const totalPages = data?.totalPages ?? 1;

  const { mutate: deleteScheduleMutate, isError: deleteError } = useDeleteSchedule();

  if (isLoading)
    return (
      <div className="hidden md:flex flex-col w-[320px] lg:w-[400px] h-[654px] bg-secondary pt-4 shadow-md rounded-lg">
        <h2 className="font-bold text-20 p-4">일정 리스트</h2>
        <ul className="w-full border-y divide-y divide-text-gray">
          <Skeleton />
        </ul>
      </div>
    );

  if (isError)
    return (
      <div className="hidden md:flex flex-col w-[320px] lg:w-[400px] h-[654px] bg-secondary pt-4 shadow-md rounded-lg">
        <h2 className="font-bold text-20 p-4">일정 리스트</h2>
        <ul className="w-full border-y divide-y divide-text-gray">
          <p className="text-16">에러가 발생했습니다.</p>
        </ul>
      </div>
    );

  if (deleteError) {
    ErrorToast('일정 삭제에 실패했습니다. 다시 시도해 주세요!');
  }

  return (
    <div className="w-full h-full bg-secondary pt-4 shadow-md rounded-lg">
      <div className="w-full h-full">
        <h2 className="font-bold text-20 p-4 select-none">일정 리스트</h2>
        <ul className="w-full border-y divide-y divide-text-gray">
          {schedules.map((list) => (
            <li
              key={list.eventId}
              className="flex justify-between lists-center px-4 py-3 items-center cursor-pointer"
            >
              <div className="flex-1" onClick={() => router.push(`/schedule/edit/${list.eventId}`)}>
                <span className="font-bold text-16">{list.title}</span>
                <span className="block text-text-secondary font-normal">{list.eventAt}</span>
              </div>
              {!list.isAnniversary && (
                <DeleteBtn
                  onClick={() => deleteScheduleMutate({ matchId: matchId!, eventId: list.eventId })}
                />
              )}
            </li>
          ))}
        </ul>
      </div>

      {/* 페이지네이션 */}
      <div className="sticky bottom-[70px] flex justify-between items-center py-3 px-4 border-t border-gray bg-secondary rounded-b-md">
        <PrevBtn page={page} setPage={setPage} />
        <div className="bg-calendar w-8 h-8 rounded-full flex justify-center items-center">
          <span>{page + 1}</span>
        </div>
        <NextBtn page={page} setPage={setPage} totalPages={totalPages} />
      </div>
    </div>
  );
}
