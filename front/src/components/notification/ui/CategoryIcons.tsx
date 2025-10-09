import { categoryType } from '@/types/notification';
import { CalendarCheck, MessageSquare, UsersRound } from 'lucide-react';
import React from 'react';

interface Props {
  category: categoryType;
  className?: string;
}
export default function CategoryIcons({ category, className }: Props) {
  switch (category) {
    case 'EVENT':
      return <CalendarCheck className={`${className}`} />;
    case 'QUESTION':
      return <MessageSquare className={`${className}`} />;
    case 'MATCH':
      return <UsersRound className={`${className}`} />;
  }
}
