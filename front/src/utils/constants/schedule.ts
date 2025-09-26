import { Schedule } from '@/types/scheduleType';

//repeatType를 key:value로 분리
export const repeatTypeLabels: Record<Schedule['repeatType'], string> = {
  none: '',
  weekly: '매주 반복',
  monthly: '매월 반복',
  yearly: '매년 반복',
};
