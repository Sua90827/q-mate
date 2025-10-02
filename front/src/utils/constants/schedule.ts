import { ScheduleEvent } from '@/types/scheduleType';

//repeatType를 key:value로 분리
export const repeatTypeLabels: Record<ScheduleEvent['repeatType'], string> = {
  NONE: '',
  WEEKLY: '매주 반복',
  MONTHLY: '매월 반복',
  YEARLY: '매년 반복',
};
