'use client';
import React, { useState } from 'react';
import Image from 'next/image';
import { Button } from '../common/Button';
import { Copy } from 'lucide-react';
import InviteCopyErrorModal from './ui/InviteCopyErrorModal';

export default function Invite() {
  const [code, setCode] = useState();
  const [open, setOpen] = useState(false);

  // Clipboard API를 이용한 복사
  const handleCopyClipBoard = async (text: string) => {
    try {
      await navigator.clipboard.writeText(text);
    } catch (e) {
      // 실패 시 모달 오픈
      setOpen(true);
    }
  };

  return (
    <>
      <div className="mb-10 text-center">
        <p className="font-Gumi text-24">함께할 사람에게</p>
        <p className="font-Gumi text-24">초대코드를 공유해주세요!</p>
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

      <Image src="/images/bubbley/bubbley_baby.png" alt="버블리 캐릭터" width={120} height={167} />
      <Button variant="invite" className="w-[300px] mt-10 z-10">
        등록하기
      </Button>

      <InviteCopyErrorModal open={open} setOpen={setOpen} />
    </>
  );
}
