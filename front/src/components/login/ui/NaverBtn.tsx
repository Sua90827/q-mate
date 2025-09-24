import React from 'react';
import { Button } from '../../common/Button';
import Image from 'next/image';
import Link from 'next/link';

export default function NaverBtn() {
  return (
    <Button variant="icon" className="bg-naver w-[295px] hover:bg-naver/80 text-secondary" asChild>
      <Link href="/signup/onboarding">
        <Image src="/images/social/naverLogo.png" width={12} height={11} alt="네이버 로그인" />
        네이버 로그인
      </Link>
    </Button>
  );
}
