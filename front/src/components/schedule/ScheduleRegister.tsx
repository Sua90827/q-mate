'use client';
import React, { useRef, useState } from 'react';
import { DatePicker } from '../common/DatePicker';
import RepeatSelector from './ui/RepeatSelector';
import { Button } from '../common/Button';
import Link from 'next/link';
import { useCreateSchedule } from '@/hooks/useSchedule';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { ErrorToast } from '../common/CustomToast';
import { useRouter } from 'next/navigation';

export default function ScheduleRegister() {
  const matchId = useMatchIdStore((state) => state.matchId);

  const [date, setDate] = useState<string | undefined>(undefined);
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');

  const repeatTypeRef = useRef<'WEEKLY' | 'MONTHLY' | 'YEARLY' | 'NONE'>('NONE');
  const alarmOptionRef = useRef<'NONE' | 'WEEK_BEFORE' | 'THREE_DAYS_BEFORE' | 'SAME_DAY'>('NONE');
  const router = useRouter();

  const {
    mutate: createScheduleMutate,
    isPending: isCreating,
    isError: isCreateError,
  } = useCreateSchedule();

  const handleCreate = () => {
    if (!date) return;

    createScheduleMutate(
      {
        matchId: matchId!,
        title,
        description,
        eventAt: date,
        repeatType: repeatTypeRef.current,
        alarmOption: alarmOptionRef.current,
      },
      {
        onSuccess: () => {
          router.push('/schedule');
        },
      },
    );
    if (isCreateError) {
      ErrorToast('일정 등록에 실패했습니다. 다시 시도해주세요.');
    }
  };

  return (
    <div className="w-full h-full flex justify-center items-center">
      <form
        className="flex w-[310px] sm:px-0 sm:w-[400px] flex-col gap-5"
        onSubmit={(e) => {
          e.preventDefault();
          handleCreate();
        }}
      >
        <DatePicker label="날짜를 선택해주세요." schedule onSelect={(d) => setDate(d)} />

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

        <RepeatSelector
          titleLabel="반복 선택"
          options={[
            { label: '반복 없음', value: 'NONE' },
            { label: '매년', value: 'YEARLY' },
            { label: '매달', value: 'MONTHLY' },
            { label: '매주', value: 'WEEKLY' },
          ]}
          onChange={(val) => {
            repeatTypeRef.current = val as 'NONE' | 'YEARLY' | 'MONTHLY' | 'WEEKLY';
          }}
        />

        <RepeatSelector
          titleLabel="알림 선택"
          options={[
            { label: '알림없음', value: 'NONE' },
            { label: '일주일전', value: 'WEEK_BEFORE' },
            { label: '3일전', value: 'THREE_DAYS_BEFORE' },
            { label: '당일', value: 'SAME_DAY' },
          ]}
          onChange={(val) => {
            alarmOptionRef.current = val as
              | 'NONE'
              | 'WEEK_BEFORE'
              | 'THREE_DAYS_BEFORE'
              | 'SAME_DAY';
          }}
        />

        <div className="flex gap-6 sm:gap-10 mt-3">
          <Button variant="outline" size="lg" asChild className="w-[142px] sm:w-[180px]">
            <Link href="/schedule">취소하기</Link>
          </Button>
          <Button size="lg" className="w-[142px] sm:w-[180px]" type="submit" disabled={isCreating}>
            {isCreating ? '등록 중...' : '등록하기'}
          </Button>
        </div>
      </form>
    </div>
  );
}
