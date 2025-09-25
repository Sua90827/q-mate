'use client';
import { useScheduleList } from '@/hooks/useSchedule';
import React, { useState } from 'react';
import DeleteBtn from '../common/DeleteBtn';
import TrashCan from '../common/TrashCan';

export default function ScheduleListMob() {
  const [isDelete, setIsDelete] = useState(false);
  const { data, isLoading, isError } = useScheduleList();

  if (isLoading) return <div>불러오는 중...</div>;
  if (isError) return <div>에러가 발생했습니다.</div>;

  const schedules = data ?? [];

  const today = new Date();

  const nextSchedules = schedules.filter((item) => {
    const eventDate = new Date(item.eventAt);
    return eventDate >= today;
  });

  return (
    <div className=" w-full h-full ">
      <div className="flex justify-center items-center h-[70px] relative">
        <p className="text-20 font-Gumi select-none">일정</p>
        <TrashCan onClick={() => setIsDelete((prev) => !prev)} />
      </div>

      <div>
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
              {isDelete && <DeleteBtn />}
            </li>
          ))}
        </ul>
      </div>
    </div>
  );
}
