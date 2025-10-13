'use client';
import Image from 'next/image';
import React, { useEffect, useState } from 'react';
import { Copy } from 'lucide-react';
import { useParams, useSearchParams, useRouter } from 'next/navigation';
import NoticeModal from '../common/NoticeModal';
import { useCreateInviteCode } from '@/hooks/useInvite';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useMatchInfo } from '@/hooks/useMatches';
import { useSelectedStore } from '@/store/useSelectedStore';
import Loader from '../common/Loader';

type InviteErrorType = 'copy' | 'code' | null;

export default function Invite() {
  const [code, setCode] = useState('');
  const [open, setOpen] = useState(false);
  const [errorOpen, setErrorOpen] = useState(false);
  const [ErrorType, setErrorType] = useState<InviteErrorType>(null);

  const { relationType } = useParams<{ relationType: string }>();
  const searchParams = useSearchParams();
  const startDate = searchParams.get('date'); // 연인일 경우에만 존재
  const [loading, setLoading] = useState(false);

  const matchId = useMatchIdStore((state) => state.matchId);
  const setMatchId = useMatchIdStore((state) => state.setMatchId);
  const setSelectedMenu = useSelectedStore((s) => s.setSelectedMenu);

  const { mutate: createCode } = useCreateInviteCode();
  const { data } = useMatchInfo(matchId!, {
    refetchInterval: 2000,
  });
  const router = useRouter();

  // 초대코드 생성
  useEffect(() => {
    if (!relationType) return;

    createCode(
      {
        relationType: relationType as 'COUPLE' | 'FRIEND',
        startDate: relationType === 'COUPLE' ? startDate : null,
      },
      {
        onSuccess: (data) => {
          setCode(data.inviteCode);
          setMatchId(data.matchId);
        },
        onError: () => {
          setErrorType('code');
          setErrorOpen(true);
        },
      },
    );
  }, [relationType, startDate, createCode, setMatchId]);

  // 클립보드 복사
  const handleCopyClipBoard = async (text: string) => {
    try {
      await navigator.clipboard.writeText(text);
      setOpen(true);
    } catch {
      setErrorType('copy');
      setErrorOpen(true);
    }
  };

  useEffect(() => {
    if (data?.status === 'ACTIVE') {
      setSelectedMenu('home');
      router.replace('/main');
    }
  }, [data?.status]);

  if (loading) return <Loader />;

  return (
    <>
      <div className="mb-10 text-center">
        <p className="font-Gumi text-24">함께할 사람에게</p>
        <p className="font-Gumi text-24">초대 코드를 공유해주세요!</p>
        <div className="relative">
          <div onClick={() => handleCopyClipBoard(code!)}>
            <input
              type="text"
              value={code}
              className="rounded-md pl-4 bg-secondary font-Pre text-14 py-4 mt-7 w-[250px] select-none"
              readOnly
            />
            <Copy className="!w-5 !h-5 text-text-secondary absolute top-12 right-8 cursor-pointer" />
          </div>
        </div>
      </div>

      <Image
        src="/images/bubbley/bubbley_baby.png"
        alt="버블리 캐릭터"
        width={120}
        height={167}
        className="select-none"
      />

      <NoticeModal
        open={open}
        setOpen={setOpen}
        title="상대방을 기다리는 중이에요!"
        sub={
          <>
            상대방이 초대 코드를 입력하면 <br />
            자동으로 연결돼요
          </>
        }
        onConfirm={() => setLoading(true)}
      />
      <NoticeModal
        open={errorOpen}
        setOpen={setErrorOpen}
        danger
        title={
          ErrorType === 'copy' ? (
            <>
              복사에 실패했어요. <br />
              다시 시도해 주세요!
            </>
          ) : (
            <>
              코드 생성에 실패했어요. <br />
              새로 고침을 해주세요!
            </>
          )
        }
      />
    </>
  );
}
