export interface Schedule {
  eventId: string;
  title: string;
  eventAt: string;
  repeatType: 'none' | 'yearly';
  isAnniversary: boolean;
}

export type ScheduleResponse = Schedule[];
