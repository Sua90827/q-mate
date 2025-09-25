// utils/date.ts

import { Schedule } from '@/types/scheduleType';

// Date YYYY.MM.DD -> YYYY-MM-DD형태의 문자열로 변환
// 캘린더 날짜클릭 시 일정리스트에 사용할 키값으로 사용 용도
export const toKey = (d: Date) => {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
};

//a와 b의 날짜가 같은지 확인
export const sameMonthDay = (a: Date, b: Date) =>
  a.getMonth() === b.getMonth() && a.getDate() === b.getDate();

//a와 b의 날짜가 같은지 연,월,일 확인
export const sameDay = (a: Date, b: Date) =>
  a.getFullYear() === b.getFullYear() &&
  a.getMonth() === b.getMonth() &&
  a.getDate() === b.getDate();

//selected를 event의 날짜가 event 반복규칙에 해당하는지 확인
export const isEventOnDate = (event: Schedule, selected: Date): boolean => {
  const [y, m, d] = event.eventAt.split('-').map(Number);
  const eventDate = new Date(y, (m ?? 1) - 1, d ?? 1);

  switch (event.repeatType) {
    case 'none':
      return sameDay(eventDate, selected);
    case 'weekly':
      return eventDate.getDay() === selected.getDay();
    case 'monthly':
      return eventDate.getDate() === selected.getDate();
    case 'yearly':
      return sameMonthDay(eventDate, selected);
    default:
      return false;
  }
};
