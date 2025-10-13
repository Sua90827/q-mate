// 날짜 → 'YYYY-MM-DD' 문자열
export const dateToString = (d: Date) => {
  const y = d.getFullYear();
  const m = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  return `${y}-${m}-${day}`;
};

// 'YYYY-MM-DD' 문자열 → 로컬 기준 Date
export const stringToDate = (s: string) => {
  const [y, m, d] = s.split('-').map(Number);
  return new Date(y, m - 1, d);
};

// 해당 월 달력에 표시되는 날짜 구간(일요일~토요일)
export const getCalendarRange = (date: Date) => {
  const firstDay = new Date(date.getFullYear(), date.getMonth(), 1);
  const lastDay = new Date(date.getFullYear(), date.getMonth() + 1, 0);
  const calendarStart = new Date(firstDay);
  calendarStart.setDate(firstDay.getDate() - firstDay.getDay());
  const calendarEnd = new Date(lastDay);
  calendarEnd.setDate(lastDay.getDate() + (6 - lastDay.getDay()));
  return {
    start: calendarStart,
    end: calendarEnd,
  };
};
