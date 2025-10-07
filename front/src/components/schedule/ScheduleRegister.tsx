'use client';
import React from 'react';
import { useRouter } from 'next/navigation';
import { ErrorToast, SuccessToast } from '../common/CustomToast';
import { useCreateSchedule } from '@/hooks/useSchedule';
import { ScheduleFormPayload } from '@/types/scheduleType';
import ScheduleForm from './ui/ScheduleForm';

export default function ScheduleRegister() {
  const router = useRouter();
  const { mutate: createScheduleMutate, isPending: isCreating } = useCreateSchedule();

  const handleCreate = (payload: ScheduleFormPayload) => {
    createScheduleMutate(
      {
        ...payload,

        repeatType: payload.repeatType ?? 'NONE',
      },
      {
        onSuccess: () => {
          router.push('/schedule');
          SuccessToast('일정 등록에 성공했습니다.');
        },
        onError: () => {
          ErrorToast('일정 등록에 실패했습니다. 다시 시도해주세요.');
        },
      },
    );
  };

  return <ScheduleForm mode="create" onSubmit={handleCreate} submitting={isCreating} />;
}
