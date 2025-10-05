'use client';
import { useCallback } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { ScheduleForm } from '@/components/schedule/ui/ScheduleForm';
import { SuccessToast, ErrorToast } from '@/components/common/CustomToast';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useEventDetail, useUpdateSchedule } from '@/hooks/useSchedule';
import Loader from '@/components/common/Loader';
import { ScheduleFormPayload } from '@/types/scheduleType';

export default function Edit() {
  const matchId = useMatchIdStore((s) => s.matchId)!;
  const { eventId } = useParams<{ eventId: string }>();
  const id = Number(eventId);
  const router = useRouter();

  const { data: detail, isLoading } = useEventDetail(matchId, id);
  const { mutate: updateSchedule, isPending } = useUpdateSchedule(matchId, id);

  const handleUpdate = useCallback(
    (payload: ScheduleFormPayload) => {
      updateSchedule(
        {
          title: payload.title,
          description: payload.description ?? '',
          eventAt: payload.eventAt,
          repeatType: payload.repeatType,
          alarmOption: payload.alarmOption,
        },
        {
          onSuccess: () => {
            SuccessToast('일정이 수정되었습니다.');
            router.push('/schedule');
          },
          onError: () => {
            ErrorToast('일정 수정에 실패했습니다. 다시 시도해주세요.');
          },
        },
      );
    },
    [updateSchedule, router],
  );

  if (isLoading || !detail) return <Loader />;

  return (
    <ScheduleForm
      mode="edit"
      initial={{
        title: detail.title,
        description: detail?.description,
        eventAt: detail.eventAt,
        repeatType: detail?.repeatType,
        alarmOption: detail?.alarmOption,
        isAnniversary: detail?.isAnniversary,
      }}
      submitting={isPending}
      onSubmit={handleUpdate}
    />
  );
}
