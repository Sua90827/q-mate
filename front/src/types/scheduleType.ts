export type RepeatType = 'WEEKLY' | 'MONTHLY' | 'YEARLY' | 'NONE';
export type AlarmOption = 'NONE' | 'WEEK_BEFORE' | 'THREE_DAYS_BEFORE' | 'SAME_DAY';
export interface ScheduleEvent {
  eventId: number;
  title: string;
  description: string;
  eventAt: string;
  repeatType: RepeatType;
  alarmOption: AlarmOption;
  isAnniversary: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface Pageable {
  pageNumber: number;
  pageSize: number;
  sort: { property: string; direction: 'ASC' | 'DESC' }[];
}

export interface ScheduleResponse {
  content: ScheduleEvent[];
  pageable: Pageable;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
  numberOfElements: number;
  empty: boolean;
}
// 스케줄 form type
export type ScheduleFormInitial = Partial<{
  title: string;
  description: string;
  eventAt: string;
  repeatType: RepeatType;
  alarmOption: AlarmOption;
  isAnniversary: boolean;
}>;

export type ScheduleFormPayload = {
  matchId: number;
  title: string;
  description: string;
  eventAt: string;
  repeatType: RepeatType;
  alarmOption: AlarmOption;
};
export type EventMonthResponse = {
  year: number;
  month: number;
  days: EventDay[];
};
export type EventDay = {
  eventId: number;
  eventAt: string;
  anniversary: boolean;
};
