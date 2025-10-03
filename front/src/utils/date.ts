// Date YYYY.MM.DD -> YYYY-MM-DD형태의 문자열로 변환

import { ScheduleEvent } from '@/types/scheduleType';
import { format } from 'date-fns/format';

// 캘린더 날짜클릭 시 일정리스트에 사용할 키값으로 사용 용도
export const toKey = (d: Date) => format(d, 'yyyy-MM-dd');

//a와 b의 날짜가 같은지 확인
export const sameMonthDay = (a: Date, b: Date) =>
  a.getMonth() === b.getMonth() && a.getDate() === b.getDate();

//a와 b의 날짜가 같은지 연,월,일 확인
export const sameDay = (a: Date, b: Date) =>
  a.getFullYear() === b.getFullYear() &&
  a.getMonth() === b.getMonth() &&
  a.getDate() === b.getDate();

//selected를 event의 날짜가 event 반복규칙에 해당하는지 확인
export const isEventOnDate = (event: ScheduleEvent, selected: Date): boolean => {
  const [y, m, d] = event.eventAt.split('-').map(Number);
  const eventDate = new Date(y, (m ?? 1) - 1, d ?? 1);

  switch (event.repeatType) {
    case 'NONE':
      return sameDay(eventDate, selected);
    case 'WEEKLY':
      return eventDate.getDay() === selected.getDay();
    case 'MONTHLY':
      return eventDate.getDate() === selected.getDate();
    case 'YEARLY':
      return sameMonthDay(eventDate, selected);
    default:
      return false;
  }
};
