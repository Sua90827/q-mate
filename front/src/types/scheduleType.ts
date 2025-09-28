export interface Schedule {
  eventId: string;
  title: string;
  eventAt: string;
  repeatType: 'none' | 'weekly' | 'monthly' | 'yearly';
  isAnniversary: boolean;
}

export type ScheduleResponse = Schedule[];

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
