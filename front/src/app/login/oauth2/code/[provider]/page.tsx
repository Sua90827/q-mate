'use client';
import { useEffect } from 'react';
import { useSearchParams, useRouter } from 'next/navigation';
import axios from 'axios';
import Loader from '@/components/common/Loader';

export default function GoogleCallback() {
  const router = useRouter();
  const searchParams = useSearchParams();

  useEffect(() => {
    const code = searchParams.get('code');
    if (!code) {
      // ❌ code가 없으면 로그인 페이지로 되돌림
      router.replace('/login');
      return;
    }

    const exchangeCode = async () => {
      try {
        // ✅ 백엔드 쿠키/토큰 없이 단순히 성공 여부만 확인
        const res = await axios.post('/auth/exchange', { code }, { withCredentials: true });

        if (res.status === 200) {
          console.log('✅ 로그인 성공 (임시 처리)');
          router.replace('/main'); // 성공 시 메인으로 이동
        } else {
          console.error('❌ 로그인 실패 응답:', res.status);
          router.replace('/login');
        }
      } catch (err) {
        console.error('❌ 로그인 중 오류 발생:', err);
        router.replace('/login');
      }
    };

    exchangeCode();
  }, [searchParams, router]);

  return <Loader />;
}
