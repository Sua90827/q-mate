'use client';
import { useDeleteSchedule, useScheduleList } from '@/hooks/useSchedule';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { X } from 'lucide-react';
import { Skeleton } from '../ui/skeleton';
import DeleteBtn from '../common/DeleteBtn';
import PrevBtn from '../common/PrevBtn';
import NextBtn from '../common/NextBtn';
import { useMemo, useState } from 'react';

export default function ScheduleListWeb() {
  const matchId = useMatchIdStore((state) => state.matchId);
  const { data, isLoading, isError } = useScheduleList(matchId!);
  const [page, setPage] = useState<number>(0);
  const pageSize = 20;

  const schedules = data?.content ?? [];
  const today = new Date();
  const oneYearLater = new Date(new Date().setFullYear(today.getFullYear() + 1));

  const nextSchedules = schedules.filter((item) => {
    const eventDate = new Date(item.eventAt);
    return eventDate >= today && eventDate <= oneYearLater;
  });

  // 페이지네이션 처리
  const schedule = useMemo(
    () => nextSchedules.slice(page * pageSize, (page + 1) * pageSize),
    [nextSchedules, page],
  );

  // 총 페이지 수
  const totalPages = Math.ceil(schedule.length / pageSize);

  const {
    mutate: deleteScheduleMutate,
    isPending: deleting,
    isError: deleteError,
  } = useDeleteSchedule();

  if (isLoading)
    return (
      <div className="hidden md:flex flex-col w-[320px] lg:w-[400px] h-full bg-secondary py-4 shadow-md rounded-lg">
        <h2 className="font-bold text-20 p-4">일정 리스트</h2>
        <ul className="w-full border-y divide-y divide-text-gray">
          <Skeleton />
        </ul>
      </div>
    );
  if (isError)
    return (
      <div>
        <div className="hidden md:flex flex-col w-[320px] lg:w-[400px] h-full bg-secondary py-4 shadow-md rounded-lg">
          <h2 className="font-bold text-20 p-4">일정 리스트</h2>
          <ul className="w-full border-y divide-y divide-text-gray">
            <p className="text-16"> 에러가 발생했습니다.</p>
          </ul>
        </div>
      </div>
    );

  return (
    <div className="hidden md:flex flex-col w-[320px] lg:w-[400px] h-full bg-secondary py-4 shadow-md rounded-lg">
      <div className="w-full h-full">
        <h2 className="font-bold text-20 p-4">일정 리스트</h2>
        <ul className="w-full border-y divide-y divide-text-gray">
          {nextSchedules.map((list) => (
            <li
              key={list.eventId}
              className="flex justify-between lists-center px-4 py-3 items-center"
            >
              <div>
                <span className="font-bold text-16">{list.title}</span>
                <span className="block text-text-secondary font-normal">{list.eventAt}</span>
              </div>
              {list.isAnniversary === false && (
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
