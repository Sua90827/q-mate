'use client';
import React, { useRef, useState } from 'react';

import Link from 'next/link';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { DatePicker } from '@/components/common/DatePicker';
import RepeatSelector from './RepeatSelector';
import { Button } from '@/components/common/Button';
import {
  AlarmOption,
  RepeatType,
  ScheduleFormInitial,
  ScheduleFormPayload,
} from '@/types/scheduleType';

export type ScheduleFormProps = {
  mode: 'create' | 'edit';
  initial?: ScheduleFormInitial;
  submitting?: boolean;
  onSubmit: (payload: ScheduleFormPayload) => void | Promise<void>;
};

export function ScheduleForm({
  mode = 'create',
  initial,
  submitting,
  onSubmit,
}: ScheduleFormProps) {
  const matchId = useMatchIdStore((state) => state.matchId);

  const [date, setDate] = useState<string | undefined>(initial?.eventAt);
  const [title, setTitle] = useState(initial?.title ?? '');
  const [description, setDescription] = useState(initial?.description ?? '');

  const repeatTypeRef = useRef<RepeatType>(initial?.repeatType ?? 'NONE');
  const alarmOptionRef = useRef<AlarmOption>(initial?.alarmOption ?? 'NONE');

  const isAnniversary = initial?.isAnniversary === true;

  React.useEffect(() => {
    if (typeof initial?.title === 'string') setTitle(initial.title);
    if (typeof initial?.description === 'string') setDescription(initial.description);
    if (typeof initial?.eventAt === 'string') setDate(initial.eventAt);
    if (initial?.repeatType) repeatTypeRef.current = initial.repeatType;
    if (initial?.alarmOption) alarmOptionRef.current = initial.alarmOption;
    // if (initial?.isAnniversary) repeatTypeRef.current = initial.repeatType;
  }, [initial]);

  const handleSubmit = async () => {
    if (!date) return;
    const repeatType = repeatTypeRef.current;
    const alarmOption = alarmOptionRef.current;
    const base = {
      matchId: matchId!,
      title,
      description,
      eventAt: date,
      alarmOption,
    } as const;

    const payload: ScheduleFormPayload = {
      ...base,
      repeatType: isAnniversary ? initial.repeatType! : repeatType,
    };
    await onSubmit(payload);
  };

  return (
    <div className="w-full h-full flex justify-center items-center">
      <form
        className="flex w-[310px] sm:px-0 sm:w-[400px] flex-col gap-5"
        onSubmit={(e) => {
          e.preventDefault();
          void handleSubmit();
        }}
      >
        <DatePicker
          label="날짜를 선택해주세요."
          schedule
          onSelect={(d) => setDate(d)}
          initialDate={date}
        />

        <input
          type="text"
          value={title}
          placeholder="일정을 입력해주세요."
          className="shadow-box py-2 pl-3 w-full"
          onChange={(e) => setTitle(e.target.value)}
        />

        <textarea
          placeholder="설명을 입력해주세요"
          value={description}
          className="w-full h-[130px] shadow-box pt-3 pl-3 resize-none"
          onChange={(e) => setDescription(e.target.value)}
        />

        {!isAnniversary && (
          <RepeatSelector
            key={`repeat-${initial?.repeatType ?? 'NONE'}`}
            titleLabel="반복 선택"
            options={[
              { label: '반복 없음', value: 'NONE' },
              { label: '매년', value: 'YEARLY' },
              { label: '매달', value: 'MONTHLY' },
              { label: '매주', value: 'WEEKLY' },
            ]}
            defaultValue={initial?.repeatType ?? 'NONE'}
            onChange={(val) => {
              repeatTypeRef.current = val as RepeatType;
            }}
          />
        )}

        <RepeatSelector
          key={`alarm-${initial?.alarmOption ?? 'NONE'}`}
          titleLabel="알림 선택"
          options={[
            { label: '알림없음', value: 'NONE' },
            { label: '일주일전', value: 'WEEK_BEFORE' },
            { label: '3일전', value: 'THREE_DAYS_BEFORE' },
            { label: '당일', value: 'SAME_DAY' },
          ]}
          defaultValue={initial?.alarmOption ?? 'NONE'}
          onChange={(val) => {
            alarmOptionRef.current = val as AlarmOption;
          }}
        />

        <div className="flex gap-6 sm:gap-10 mt-3">
          <Button variant="outline" size="lg" asChild className="w-[142px] sm:w-[180px]">
            <Link href={'/schedule'}>취소하기</Link>
          </Button>
          <Button
            size="lg"
            className="w-[142px] sm:w-[180px]"
            type="submit"
            disabled={!!submitting}
          >
            {submitting
              ? mode === 'edit'
                ? '수정 중...'
                : '등록 중...'
              : mode === 'edit'
              ? '수정하기'
              : '등록하기'}
          </Button>
        </div>
      </form>
    </div>
  );
}

export default ScheduleForm;
