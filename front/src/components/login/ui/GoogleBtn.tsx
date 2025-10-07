import React from 'react';
import { Button } from '../../common/Button';
import Image from 'next/image';
import Link from 'next/link';

interface GoogleBtnProps {
  onSocialLogin: (provider: string) => void;
}

export default function GoogleBtn({ onSocialLogin }: GoogleBtnProps) {
  return (
    <Button
      variant="icon"
      className="text-text-secondary bg-secondary border-2 border-gray w-[295px]"
      onClick={() => onSocialLogin('google')}
      asChild
    >
      <Link href="/signup/onboarding">
        <Image src="/images/social/googleLogo.png" width={20} height={20} alt="구글 로그인" />
        구글 로그인
      </Link>
    </Button>
  );
}
