'use client';
import React, { useState } from 'react';
import Image from 'next/image';
import { Button } from '../common/Button';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import ConfirmModal from '../common/ConfirmModal';
import NoticeModal from '../common/NoticeModal';
import { useRouter } from 'next/navigation';
import { useCheckInviteCode, useCreateMatchId, useFetchLockStatus } from '@/hooks/useInvite';
import { ModalConfig } from '@/types/modal';
import { handleInviteError } from '@/utils/handleInviteError';
import { useSelectedStore } from '@/store/useSelectedStore';

export default function Invited() {
  const router = useRouter();
  const setMatchId = useMatchIdStore((state) => state.setMatchId);

  const [modal, setModal] = useState<ModalConfig>({
    open: false,
    type: null,
    title: '',
    isDanger: true,
  });
  const [code, setCode] = useState('');
  const setSelectedMenu = useSelectedStore((s) => s.setSelectedMenu);
  const { mutate: checkCode } = useCheckInviteCode();
  const { mutate: joinMatch, isPending: isJoining } = useCreateMatchId();
  const { data } = useFetchLockStatus();

  // 에러 모달 설정
  const errorConfig: Record<number, ModalConfig> = {
    400: {
      open: true,
      type: 'errorNotice',
      title: '유효하지 않은 코드입니다.',
      sub: '초대 코드를 다시 확인해 주세요.',
      isDanger: true,
    },
    401: {
      open: true,
      type: 'errorConfirm',
      title: (
        <>
          로그인 정보가 유효하지 않습니다.
          <br /> 다시 로그인해주세요.
        </>
      ),
      sub: '예를 누르면 로그인 화면으로 이동됩니다.',
      isDanger: true,
      onConfirm: () => router.push('/login'),
    },
    403: {
      open: true,
      type: 'errorNotice',
      title: (
        <>
          초대 코드를 5회 이상 잘못 입력하여,
          <br /> 24시간 동안 입력과 생성이 제한됩니다.
        </>
      ),
      isDanger: true,
    },
    404: {
      open: true,
      type: 'errorNotice',
      title: (
        <>
          유효하지 않은 초대입니다.
          <br /> 새로운 초대를 받아 다시 시도해 주세요.
        </>
      ),
      isDanger: true,
    },
    409: {
      open: true,
      type: 'errorNotice',
      title: '매칭 중 문제가 발생했습니다.',
      sub: '이미 매칭에 속해 있거나 참여 중 오류가 발생했습니다.',
      isDanger: true,
    },
  };

  const handleJoin = () => {
    checkCode(
      { inviteCode: code },
      {
        onSuccess: (res) => {
          if (res.valid) {
            joinMatch(
              { inviteCode: code },
              {
                onSuccess: (data) => {
                  setModal({
                    open: true,
                    type: 'success',
                    title: (
                      <>
                        {data.partnerNickname}님과 함께 <br /> 이야기를 기록하시겠습니까?
                      </>
                    ),
                    onConfirm: () => {
                      setMatchId(data.matchId);
                      setSelectedMenu('home');
                      router.replace('/main');
                    },
                  });
                },
                onError: (error) =>
                  handleInviteError(error, setModal, errorConfig, data?.remainingSeconds),
              },
            );
          }
        },
        onError: () => {
          setModal({
            open: true,
            type: 'errorNotice',
            title: (
              <>
                유효하지 않은 코드입니다.
                <br /> 올바른 코드를 입력해주세요.
              </>
            ),
            isDanger: true,
          });
        },
      },
    );
  };

  return (
    <>
      <div className="mb-10 text-center">
        <p className="font-Gumi text-24">초대 코드를</p>
        <p className="font-Gumi text-24">등록해주세요</p>
        <input
          type="text"
          placeholder="초대 코드 입력"
          className="rounded-md pl-4 bg-secondary font-Pre text-14 py-4 mt-7 w-[250px]"
          onChange={(e) => setCode(e.target.value)}
        />
      </div>
      <Image src="/images/bubbley/bubbley_baby.png" alt="버블리 캐릭터" width={120} height={167} />
      <Button
        variant="invite"
        className="w-[300px] mt-10 z-10"
        onClick={handleJoin}
        disabled={isJoining}
      >
        {isJoining ? '등록 중...' : '등록하기'}
      </Button>

      {/* 성공 / Confirm Error */}
      {(modal.type === 'success' || modal.type === 'errorConfirm') && (
        <ConfirmModal
          open={modal.open}
          setOpen={(open) => setModal((prev) => ({ ...prev, open }))}
          title={modal.title}
          sub={modal.sub}
          isDanger={modal.isDanger}
          onConfirm={modal.onConfirm ?? (() => {})}
        />
      )}

      {/* Notice Error */}
      {modal.type === 'errorNotice' && (
        <NoticeModal
          open={modal.open}
          setOpen={(open) => setModal((prev) => ({ ...prev, open }))}
          title={modal.title}
          sub={modal.sub}
          danger={modal.isDanger}
        />
      )}
    </>
  );
}
