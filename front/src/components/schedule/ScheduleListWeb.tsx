'use client';
import { useScheduleList } from '@/hooks/useSchedule';
import { X } from 'lucide-react';

export default function ScheduleListWeb() {
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
    <div className="hidden md:flex flex-col w-[320px] lg:w-[400px] h-full bg-secondary py-4 shadow-md rounded-lg">
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
            <X className="text-text-secondary !w-5 !h-5 mr-4" />
          </li>
        ))}
      </ul>
    </div>
  );
}
