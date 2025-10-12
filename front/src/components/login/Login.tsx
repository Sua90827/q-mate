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
import { useSelectedStore } from '@/store/useSelectedStore';
import { googleLogin } from '@/utils/googleLogin';
import { buildNaverAuthorizeUrl } from '@/utils/buildNaverAuthUrl';

const validateEmail = (v: string) => /\S+@\S+\.\S+/.test(v);
const validatePassword = (v: string) =>
  v.length >= 8 && /[0-9]/.test(v) && /[a-zA-Z]/.test(v) && /[^a-zA-Z0-9]/.test(v);

type LoginErrorType = 'normal' | 'social' | null;

export default function Login() {
  const [isEmailValid, setEmailValid] = useState(false);
  const [isPasswordValid, setPasswordValid] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [open, setOpen] = useState(false);
  const [loginErrorType, setLoginErrorType] = useState<LoginErrorType>(null);
  const syncPushOnLogin = useSyncPushOnLogin();

  const isFormValid = isEmailValid && isPasswordValid;
  const router = useRouter();

  const setMatchId = useMatchIdStore((state) => state.setMatchId);
  const setAccessToken = useAuthStore((state) => state.setAccessToken);
  const setSelectedMenu = useSelectedStore((state) => state.setSelectedMenu);
  const { mutate: loginUserMutate, isPending: isLoginLoading } = useLoginUser();

  const naverLogin = () => {
    window.location.href = buildNaverAuthorizeUrl();
  };

  // 일반 로그인
  const handleLogin = () => {
    loginUserMutate(
      { email, password },
      {
        onSuccess: async (data) => {
          try {
            // 1) 토큰을 가장 먼저 저장 (이후 호출들에서 Authorization 헤더가 붙도록)
            setAccessToken(data.accessToken);
            const accessTokenTime = Date.now() + data.accessTokenExpiresIn * 1000;
            localStorage.setItem('accessTokenTime', String(accessTokenTime));

            // 2) 푸시 동기화 (보호 API일 수 있으므로 토큰 저장 후)
            try {
              await syncPushOnLogin();
            } catch (error) {
              console.warn('[Push Sync Failed]', error);
            }

            if (data.user.currentMatchId) {
              // 매치 아이디 셋팅
              setMatchId(data.user.currentMatchId);

              // 서버에서 현재 exp 조회 (이제 Authorization 헤더가 포함됨)
              const petInfo = await fetchPetInfo(data.user.currentMatchId);
              // 현재 exp 셋팅
              localStorage.setItem('prevExp', String(petInfo.exp));

              // 메뉴 메인 선택
              setSelectedMenu('home');
              router.push('/main');
            } else {
              router.push('/invite');
            }
          } catch (e) {
            // 혹시 모를 예외에 대한 방어
            console.error('[Login onSuccess handler error]', e);
            setLoginErrorType('normal');
            setOpen(true);
          }
        },
        onError: () => {
          setLoginErrorType('normal');
          setOpen(true);
        },
      },
    );
  };

  return (
    <div className="w-full h-full flex flex-col gap-3 items-center justify-center pt-[70px] sm:pt-[0px] sm:pb-[70px]">
      {isLoginLoading && <Loader />}
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
          type="submit"
          className="w-[295px]"
          disabled={!isFormValid}
          variant="primary"
          onClick={handleLogin}
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
      <GoogleBtn onSocialLogin={googleLogin} />
      <NaverBtn onSocialLogin={naverLogin} />
      <NoticeModal
        open={open}
        setOpen={setOpen}
        title={
          loginErrorType === 'social' ? (
            <>
              소셜 로그인에 실패했습니다.
              <br />
              다시 시도해주세요.
            </>
          ) : (
            <>
              로그인에 실패했습니다.
              <br />
              입력 정보를 다시 확인해주세요.
            </>
          )
        }
        danger
      />
    </div>
  );
}
