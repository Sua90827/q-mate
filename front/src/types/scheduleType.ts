export interface Schedule {
  eventId: string;
  title: string;
  eventAt: string;
  repeatType: 'none' | 'weekly' | 'monthly' | 'yearly';
  isAnniversary: boolean;
}

export type ScheduleResponse = Schedule[];
