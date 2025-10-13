import React from 'react';
import { Button } from '../../common/Button';
import Image from 'next/image';

interface NaverBtnProps {
  onSocialLogin: () => void;
}

export default function NaverBtn({ onSocialLogin }: NaverBtnProps) {
  return (
    <Button
      variant="icon"
      className="bg-naver w-[295px] hover:bg-naver/80 text-secondary"
      onClick={onSocialLogin}
    >
      <Image src="/images/social/naverLogo.png" width={12} height={11} alt="네이버 로그인" />
      네이버 로그인
    </Button>
  );
}
