'use client';
import { useDeleteSchedule, useScheduleList } from '@/hooks/useSchedule';
import React, { useMemo, useState } from 'react';
import DeleteBtn from '../common/DeleteBtn';
import TrashCan from '../common/TrashCan';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { ErrorToast } from '../common/CustomToast';
import { Skeleton } from '../ui/skeleton';
import Loader from '../common/Loader';
import PrevBtn from '../common/PrevBtn';
import NextBtn from '../common/NextBtn';

export default function ScheduleListMob() {
  const [isDelete, setIsDelete] = useState(false);
  const [page, setPage] = useState<number>(0);
  const pageSize = 20;
  const matchId = useMatchIdStore((state) => state.matchId);
  const { data, isLoading, isError } = useScheduleList(matchId!);
  const schedules = data?.content ?? [];
  const today = new Date();

  const nextSchedules = schedules.filter((item) => {
    const eventDate = new Date(item.eventAt);
    return eventDate >= today;
  });
  const {
    mutate: deleteScheduleMutate,
    isPending: deleting,
    isError: deleteError,
  } = useDeleteSchedule();

  // 페이지네이션 처리
  const schedule = useMemo(
    () => nextSchedules.slice(page * pageSize, (page + 1) * pageSize),
    [nextSchedules, page],
  );

  // 총 페이지 수
  const totalPages = Math.ceil(schedule.length / pageSize);

  if (deleteError) {
    ErrorToast('삭제에 실패했습니다. 다시 시도해 주세요.');
  } else if (deleting) {
    <Loader />;
  }
  if (isLoading)
    return (
      <div>
        <div className=" w-full h-full ">
          <div className="flex justify-center items-center h-[70px] relative">
            <p className="text-20 font-Gumi select-none">일정</p>
            <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
          </div>
          <Skeleton />
        </div>
      </div>
    );
  if (isError)
    return (
      <div>
        <div className=" w-full h-full ">
          <div className="flex justify-center items-center h-[70px] relative">
            <p className="text-20 font-Gumi select-none">일정</p>
            <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
          </div>

          <div>
            <ul className="w-full border-y divide-y divide-text-gray">에러가 발생했습니다.</ul>
          </div>
        </div>
      </div>
    );

  return (
    <div className=" w-full h-full ">
      <div className="flex justify-center items-center h-[70px] relative">
        <p className="text-20 font-Gumi select-none text-theme-primary">일정</p>
        <div className="absolute right-4">
          <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
        </div>
      </div>
      <div className="w-full flex-1 h-full bg-secondary">
        <div>
          <ul className="w-full border-y divide-y divide-text-gray ">
            {nextSchedules.map((list) => (
              <li
                key={list.eventId}
                className="flex justify-between lists-center px-4 py-3 items-center"
              >
                <div>
                  <span className="font-bold text-16">{list.title}</span>
                  <span className="block text-text-secondary font-normal">{list.eventAt}</span>
                </div>
                {list.isAnniversary === false && isDelete && (
                  <DeleteBtn
                    onClick={() =>
                      deleteScheduleMutate({ matchId: matchId!, eventId: list.eventId })
                    }
                  />
                )}
              </li>
            ))}
          </ul>
        </div>
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
