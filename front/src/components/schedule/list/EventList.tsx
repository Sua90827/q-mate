'use client';
import React from 'react';
import { repeatTypeLabels } from '@/utils/constants/schedule';
import { ScheduleEvent } from '@/types/scheduleType';
import { Loader2 } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useDeleteSchedule } from '@/hooks/useSchedule';
import DeleteBtn from '@/components/common/DeleteBtn';
import { useMatchIdStore } from '@/store/useMatchIdStore';

type Props = {
  date: Date;
  items: ScheduleEvent[];
  isLoading?: boolean;
  isError?: boolean;
};

function EventList({ date, items, isLoading, isError }: Props) {
  const { mutate: deleteScheduleMutate, isError: deleteError } = useDeleteSchedule();
  const router = useRouter();
  const matchId = useMatchIdStore((state) => state.matchId);
  return (
    <div className="flex-1 border-t flex flex-col border-text-text-primary rounded-b-lg bg-secondary shadow-sm h-full px-10 overflow-hidden min-h-0">
      <h2 className="font-extrabold text-18 mt-4">
        {date.getDate()}. {date.toLocaleString('ko-KR', { weekday: 'narrow' })}
      </h2>
      {/* 리스트 내부에 스크롤 추가 */}
      <ul className="max-h-full flex-1 min-h-0 overflow-y-auto">
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
              <li key={eventId} className="flex items-center justify-between py-3 cursor-pointer">
                <div
                  className="flex items-center flex-1 min-w-0"
                  onClick={() => router.push(`/schedule/edit/${eventId}`)}
                >
                  <span
                    className={`inline-block w-1 h-11 mr-2 ${
                      isAnniversary ? 'bg-anniversary' : 'bg-calendar'
                    }`}
                  />
                  <div>
                    <div className="font-extrabold text-16 truncate">{title}</div>
                    <div className="text-12 text-text-secondary">
                      {isAnniversary ? '기념일' : '일정'}
                      {repeatType !== 'NONE' && ` · ${repeatTypeLabels[repeatType]}`}
                    </div>
                  </div>
                </div>
                {!isAnniversary && (
                  <DeleteBtn
                    onClick={() => deleteScheduleMutate({ matchId: matchId!, eventId: eventId })}
                  />
                )}
              </li>
            ))}
          </>
        )}
      </ul>
    </div>
  );
}

export default EventList;
