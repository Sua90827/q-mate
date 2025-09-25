'use client';
import React from 'react';
import { Schedule } from '@/types/scheduleType';

type Props = {
  date: Date;
  items: Schedule[];
  isLoading?: boolean;
  isError?: boolean;
};

const repeatTypeLabels: Record<Schedule['repeatType'], string> = {
  none: '',
  weekly: '매주 반복',
  monthly: '매월 반복',
  yearly: '매년 반복',
};

function EventList({ date, items, isLoading, isError }: Props) {
  if (isLoading) {
    return <div>일정을 불러오는 중입니다...</div>;
  }

  if (isError) {
    return <div>일정을 불러오는 중 오류가 발생했습니다.</div>;
  }

  return (
    <div className="flex-1 border-t border-text-text-primary bg-secondary shadow-sm h-full px-4 overflow-hidden">
      <h2 className="font-extrabold text-16 mt-4">
        {date.getDate()}. {date.toLocaleString('ko-kR', { weekday: 'narrow' })}
      </h2>
      {/* 리스트 내부에 스크롤 추가 */}
      <ul className="max-h-60 flex-1 overflow-y-auto">
        {items.length === 0 && <li>표시할 일정이 없어요.</li>}

        {items.map(({ eventId, title, isAnniversary, repeatType }) => (
          <li key={eventId} className="flex items-center py-2 border-y">
            <span
              className={`inline-block w-1 h-12 mr-2 ${
                isAnniversary ? 'bg-yellow-400' : 'bg-primary'
              }`}
            />
            <div>
              <div className="font-extrabold">{title}</div>
              <div className="text-12 text-text-secondary">
                {isAnniversary ? '기념일' : '일정'}
                {repeatType !== 'none' && ` · ${repeatTypeLabels[repeatType]}`}
              </div>
            </div>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default EventList;
