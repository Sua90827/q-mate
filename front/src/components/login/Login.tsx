'use client';
import React, { useState } from 'react';
import GoogleBtn from './ui/GoogleBtn';
import NaverBtn from './ui/NaverBtn';
import TextInput from '../common/TextInput';
import { Button } from '../common/Button';
import Link from 'next/link';
import Image from 'next/image';

/* .test() -> true/false 반환 */
const validateEmail = (v: string) => /\S+@\S+\.\S+/.test(v);
const validatePassword = (v: string) =>
  v.length >= 8 && /[0-9]/.test(v) && /[a-zA-Z]/.test(v) && /[^a-zA-Z0-9]/.test(v);

export default function Login() {
  const [isEmailValid, setEmailValid] = useState(false);
  const [isPasswordValid, setPasswordValid] = useState(false);
  const isFormValid = isEmailValid && isPasswordValid;

  return (
    <div className=" w-full h-full flex flex-col gap-3 items-center justify-center ">
      <Image src="/images/logo/day_logo.svg" alt="큐메이트" width={173} height={55} />

      <form onSubmit={(e) => e.preventDefault()} className="flex flex-col gap-3">
        <TextInput label="이메일" validate={validateEmail} setActive={setEmailValid} />
        <TextInput
          label="비밀번호"
          type="password"
          validate={validatePassword}
          setActive={setPasswordValid}
        />
        <Button className="w-[295px]" disabled={!isFormValid} variant="primary">
          로그인
        </Button>
      </form>

      <div className="my-6 flex items-center gap-3 w-[295px]">
        <div className="h-px flex-1 bg-dash" />
        <span className="px-2 text-font-16 text-text-secondary">또는</span>
        <div className="h-px flex-1 bg-dash" />
      </div>

      <Button variant="primaryOutline" className="w-[295px]" asChild>
        <Link href="/signup">회원가입</Link>
      </Button>
      <GoogleBtn />
      <NaverBtn />
    </div>
  );
}
