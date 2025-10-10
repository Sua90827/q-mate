'use client';
import { ErrorToast } from '@/components/common/CustomToast';
import { useAuthStore } from '@/store/useAuthStore';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useSelectedStore } from '@/store/useSelectedStore';

export const handleUnauthorized = () => {
  const { resetAccessToken } = useAuthStore.getState();
  const { resetMatchId } = useMatchIdStore.getState();
  const { resetSelectedMenu } = useSelectedStore.getState();

  ErrorToast('로그인이 만료되어 다시 로그인 화면으로 이동합니다.');
  resetSelectedMenu();
  localStorage.clear();
  resetMatchId();
  resetAccessToken();
  window.location.replace('/login');
};
