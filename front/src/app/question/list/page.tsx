'use client';
import QuestionListMob from '@/components/question/QuestionListMob';
import QuestionListWeb from '@/components/question/QuestionListWeb';
import { useMediaQuery } from 'react-responsive';

export default function QuestionPage() {
  const isMobile = useMediaQuery({ maxWidth: 639.2 });

  if (isMobile) {
    // 모바일: 리스트
    return <QuestionListMob />;
  }

  //웹: 리스트 + 상세
  return <QuestionListWeb />;
}
