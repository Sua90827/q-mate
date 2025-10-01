export interface ScheduleEvent {
  eventId: number;
  title: string;
  description: string;
  eventAt: string;
  repeatType: 'WEEKLY' | 'MONTHLY' | 'YEARLY' | 'NONE';
  alarmOption: 'SAME_DAY' | 'ONE_DAY_BEFORE' | 'NONE';
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

export type EventMonthResponse = {
  year: number;
  month: number;
  days: EventDay[];
};
export type EventDay = {
  eventId: number;
  eventAt: string;
  isAnniversary: boolean;
};
