'use client';
import React, { useEffect, useState } from 'react';
import Image from 'next/image';
import { Copy } from 'lucide-react';
import axios from 'axios';
import { useParams } from 'next/navigation';
import NoticeModal from '../common/NoticeModal';

export default function Invite() {
  const [code, setCode] = useState<string>('');

  const [open, setOpen] = useState(false);
  const [errorOpen, setErrorOpen] = useState(false);

  const params = useParams();

  //초대코드 가져오기
  useEffect(() => {
    const date = params.date;
    const createInviteCode = async () => {
      const res = await axios.post('/api/matches', {
        relationType: date === 'FRIEND' ? 'FRIEND' : 'COUPLE',
        startDate: date,
      });
      setCode(res.data.inviteCode);
    };
    createInviteCode();
  }, [params.date]);

  // Clipboard API를 이용한 복사
  const handleCopyClipBoard = async (text: string) => {
    try {
      await navigator.clipboard.writeText(text);
      setOpen(true);
    } catch (e) {
      // 실패 시 모달 오픈
      setErrorOpen(true);
    }
  };

  return (
    <>
      <div className="mb-10 text-center">
        <p className="font-Gumi text-24">함께할 사람에게</p>
        <p className="font-Gumi text-24">초대 코드를 공유해주세요!</p>
        <div className="relative">
          <div
            onClick={() => {
              handleCopyClipBoard(code!);
            }}
          >
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
        title="상대방을 기다리는 중이에요 🐢"
        description={
          <>
            상대방이 초대 코드를 입력하면 <br />
            자동으로 연결돼요
          </>
        }
      />
      <NoticeModal
        open={errorOpen}
        setOpen={setErrorOpen}
        danger
        title={
          <>
            복사에 실패했어요. <br />
            다시 시도해 주세요!
          </>
        }
      />
    </>
  );
}
