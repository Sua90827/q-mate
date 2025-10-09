'use client';
import React from 'react';
import { repeatTypeLabels } from '@/utils/constants/schedule';
import { ScheduleEvent } from '@/types/scheduleType';
import { Loader2 } from 'lucide-react';

type Props = {
  date: Date;
  items: ScheduleEvent[];
  isLoading?: boolean;
  isError?: boolean;
};

function EventList({ date, items, isLoading, isError }: Props) {
  return (
    <div className="flex-1 border-t flex flex-col border-text-text-primary rounded-b-lg bg-secondary shadow-sm h-full px-10 overflow-hidden">
      <h2 className="font-extrabold text-18 mt-4">
        {date.getDate()}. {date.toLocaleString('ko-kR', { weekday: 'narrow' })}
      </h2>
      {/* 리스트 내부에 스크롤 추가 */}
      <ul className="max-h- flex-1 min-h-0 overflow-y-auto">
        {isError && <li>일정을 불러오는 중 오류가 발생했습니다.</li>}
        {isLoading ? (
          <li>
            <Loader2 className="h-4 w-4 animate-spin" />
            일정을 불러오는 중...
          </li>
        ) : (
          <>
            {items.length === 0 && (
              <li className="pt-2 text-text-secondary">표시할 일정이 없어요.</li>
            )}

            {items.map(({ eventId, title, isAnniversary, repeatType }) => (
              <li key={eventId} className="flex items-center py-3 cursor-pointer">
                <span
                  className={`inline-block w-1 h-11 mr-2 ${
                    isAnniversary ? 'bg-anniversary' : 'bg-calendar'
                  }`}
                />
                <div>
                  <div className="font-extrabold text-16 ">{title}</div>
                  <div className="text-12 text-text-secondary">
                    {isAnniversary ? '기념일' : '일정'}
                    {repeatType !== 'NONE' && ` · ${repeatTypeLabels[repeatType]}`}
                  </div>
                </div>
              </li>
            ))}
          </>
        )}
      </ul>
    </div>
  );
}

export default EventList;
