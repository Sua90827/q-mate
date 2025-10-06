'use client';
import React, { useState } from 'react';
import GoogleBtn from './ui/GoogleBtn';
import NaverBtn from './ui/NaverBtn';
import TextInput from '../common/TextInput';
import { Button } from '../common/Button';
import Link from 'next/link';
import Image from 'next/image';
import { useLoginUser } from '@/hooks/useAuth';
import { useRouter } from 'next/navigation';
import NoticeModal from '../common/NoticeModal';
import Loader from '../common/Loader';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useSyncPushOnLogin } from '@/hooks/useSyncPush';
import { fetchPetInfo } from '@/api/pet';
import { useAuthStore } from '@/store/useAuthStore';

const validateEmail = (v: string) => /\S+@\S+\.\S+/.test(v);
const validatePassword = (v: string) =>
  v.length >= 8 && /[0-9]/.test(v) && /[a-zA-Z]/.test(v) && /[^a-zA-Z0-9]/.test(v);

export default function Login() {
  const [isEmailValid, setEmailValid] = useState(false);
  const [isPasswordValid, setPasswordValid] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [open, setOpen] = useState(false);
  const isFormValid = isEmailValid && isPasswordValid;
  const router = useRouter();
  const setMatchId = useMatchIdStore((state) => state.setMatchId);
  const setAccessToken = useAuthStore((state) => state.setAccessToken);
  const { mutate: loginUserMutate, isPending: isLoginLoading } = useLoginUser();
  const syncPushOnLogin = useSyncPushOnLogin();

  const HandleLogin = () => {
    loginUserMutate(
      { email, password },
      {
        onSuccess: async (data) => {
          try {
            await syncPushOnLogin();
          } catch (error) {
            console.warn('[Push Sync Failed]', error);
          }

          if (data.user.currentMatchId !== null) {
            //매치 아이디 셋팅
            setMatchId(data.user.currentMatchId);
            // 서버에서 현재 exp 조회
            const petInfo = await fetchPetInfo(data.user.currentMatchId);
            //현재 exp 셋팅
            localStorage.setItem('prevExp', String(petInfo.exp));
            //accessToken 셋팅
            setAccessToken(data.accessToken);
            router.push('/main');
          } else {
            router.push('/invite');
          }
        },
        onError: () => {
          setOpen(true);
        },
      },
    );
  };

  return (
    <div className=" w-full h-full flex flex-col gap-3 items-center justify-center">
      {isLoginLoading && (
        <div className="fixed inset-0 flex items-center justify-center bg-black/30 z-50">
          <Loader />
        </div>
      )}
      <Image src="/images/logo/day_logo.svg" alt="큐메이트" width={173} height={55} />

      <form onSubmit={(e) => e.preventDefault()} className="flex flex-col gap-3">
        <TextInput
          label="이메일"
          value={email}
          validate={validateEmail}
          setActive={setEmailValid}
          onChange={(e) => setEmail(e)}
        />
        <TextInput
          label="비밀번호"
          value={password}
          type="password"
          validate={validatePassword}
          setActive={setPasswordValid}
          onChange={(p) => setPassword(p)}
        />

        <Button
          className="w-[295px]"
          disabled={!isFormValid}
          variant="primary"
          onClick={HandleLogin}
        >
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

      <NoticeModal
        open={open}
        setOpen={setOpen}
        title={
          <>
            로그인에 실패했습니다.
            <br /> 입력 정보를 다시 확인해주세요.
          </>
        }
        danger
      />
    </div>
  );
}
