'use client';
import React, { useState } from 'react';
import GoogleBtn from './ui/GoogleBtn';
import NaverBtn from './ui/NaverBtn';
import TextInput from '../common/TextInput';
import { Button } from '../common/Button';
import Link from 'next/link';
import Image from 'next/image';

export default function Login() {
  const [active, setActive] = useState(false);
  return (
    <div className="bg-gradient-sub w-full min-h-screen flex flex-col gap-3 items-center justify-center ">
      <Image src="/logo.svg" alt="큐메이트" width={173} height={55} />

      <form onSubmit={(e) => e.preventDefault()} className="flex flex-col gap-3">
        <TextInput label="이메일" type="email" setActive={setActive} />
        <TextInput label="비밀번호" type="password" setActive={setActive} />
        <Button className="w-[295px]" disabled={active ? false : true}>
          로그인
        </Button>
      </form>

      <div className="my-6 flex items-center gap-3 w-[295px]">
        <div className="h-px flex-1 bg-dash" />
        <span className="px-2 text-font-16 text-text-secondary">또는</span>
        <div className="h-px flex-1 bg-dash" />
      </div>

      <Button variant="outline" className="w-[295px]" asChild>
        <Link href="/signup">회원가입</Link>
      </Button>
      <GoogleBtn />
      <NaverBtn />
    </div>
  );
}
