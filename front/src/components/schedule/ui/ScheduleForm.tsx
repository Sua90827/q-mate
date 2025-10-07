'use client';
import React, { useRef, useState, useEffect } from 'react';
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
import TextTextarea, { TextTextareaRef } from '@/components/question/ui/TextTextarea';

export type ScheduleFormProps = {
  mode: 'create' | 'edit';
  initial?: ScheduleFormInitial;
  submitting?: boolean;
  onSubmit: (payload: ScheduleFormPayload) => void | Promise<void>;
};

export function ScheduleForm({
  mode = 'create',
  initial,
  submitting = false,
  onSubmit,
}: ScheduleFormProps) {
  const matchId = useMatchIdStore((state) => state.matchId);
  const textareaRef = useRef<TextTextareaRef>(null);

  const [date, setDate] = useState<string | undefined>(initial?.eventAt);
  const [title, setTitle] = useState(initial?.title ?? '');
  const [description, setDescription] = useState(initial?.description ?? '');
  const [isEmpty, setIsEmpty] = useState<boolean>(false);

  const repeatTypeRef = useRef<RepeatType>(initial?.repeatType ?? 'NONE');
  const alarmOptionRef = useRef<AlarmOption>(initial?.alarmOption ?? 'NONE');
  const isAnniversary = initial?.isAnniversary === true;
  const disabled =
    submitting || !date || isEmpty || description.trim().length === 0 || title.trim().length === 0;
  useEffect(() => {
    if (!initial) return;
    setTitle(initial.title ?? '');
    setDescription(initial.description ?? '');
    setDate(initial.eventAt ?? undefined);
    repeatTypeRef.current = initial.repeatType ?? 'NONE';
    alarmOptionRef.current = initial.alarmOption ?? 'NONE';
  }, [initial]);

  const handleSubmit = async () => {
    if (!date) return;
    const payload: ScheduleFormPayload = {
      matchId: matchId!,
      title,
      description,
      eventAt: date,
      alarmOption: alarmOptionRef.current,
      repeatType: isAnniversary ? initial?.repeatType ?? 'NONE' : repeatTypeRef.current,
    };
    await onSubmit(payload);
  };

  const handleTextChange = (text: string) => {
    setDescription(text);
    setIsEmpty(text.trim().length === 0);
  };

  return (
    <div className="w-full h-full flex justify-center items-center">
      <form
        className="flex w-[310px] md:px-0 md:w-[390px] flex-col gap-5"
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
          className="shadow-box py-2 pl-3 w-full !h-[45px] !text-14"
          onChange={(e) => setTitle(e.target.value)}
          maxLength={30}
        />

        <TextTextarea
          ref={textareaRef}
          defaultValue={description ?? ''}
          placeholder="설명을 입력해주세요"
          textLength={handleTextChange}
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

        <div className="flex gap-8  mt-3">
          <Button variant="outline" size="lg" asChild className="w-[142px] md:w-[180px]">
            <Link href={'/schedule'}>취소하기</Link>
          </Button>
          <Button size="lg" className="w-[140px] md:w-[180px]" type="submit" disabled={disabled}>
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
