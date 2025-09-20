import React from 'react';
import { Button } from '../../common/Button';
import Image from 'next/image';
import Link from 'next/link';

export default function GoogleBtn() {
  return (
    <Button variant="outline" className="text-text-secondary border border-gray w-[295px]" asChild>
      <Link href="/signup/onboarding">
        <Image src="/images/social/googleLogo.png" width={20} height={20} alt="구글 로그인" />
        구글 로그인
      </Link>
    </Button>
  );
}
