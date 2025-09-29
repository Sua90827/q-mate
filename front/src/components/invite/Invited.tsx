'use client';
import React, { useState } from 'react';
import Image from 'next/image';
import { Button } from '../common/Button';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import ConfirmModal from '../common/ConfirmModal';
import { useRouter } from 'next/navigation';
import { useCreateMatchId } from '@/hooks/useInvite';
import axios from 'axios';

export default function Invited() {
  const [isOpen, setIsOpen] = useState(false);
  const [code, setCode] = useState('');
  const [name, setName] = useState('');
  const setMatchId = useMatchIdStore((state) => state.setMatchId);
  const router = useRouter();

  const { mutate: joinMatch, isPending: isJoining } = useCreateMatchId();

  const handleJoin = () => {
    joinMatch(
      { inviteCode: code },
      {
        onSuccess: (data) => {
          setName(data.partnerNickname);
          setMatchId(data.matchId);
          setIsOpen(true);
        },
        onError: (error) => {
          if (axios.isAxiosError(error)) {
            if (error.response?.status === 400) {
              // 만료된 코드, 잘못된 코드 구분 필요
            } else if (error.response?.status === 401) {
              // 인증 에러 처리
            }
          } else {
          }
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
      <ConfirmModal
        open={isOpen}
        setOpen={setIsOpen}
        title={
          <>
            {name}님과 함께 <br />
            이야기를 기록하시겠습니까?
          </>
        }
        onConfirm={() => {
          router.push('/main');
        }}
      />
    </>
  );
}
