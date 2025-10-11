'use client';
import { ErrorToast } from '@/components/common/CustomToast';
import { useAuthStore } from '@/store/useAuthStore';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useSelectedStore } from '@/store/useSelectedStore';
import { logoutUser } from '@/api/auth';

export const handleUnauthorized = async () => {
  const { resetAccessToken } = useAuthStore.getState();
  const { resetMatchId } = useMatchIdStore.getState();
  const { resetSelectedMenu } = useSelectedStore.getState();

  ErrorToast('로그인이 만료되어 다시 로그인 화면으로 이동합니다.');

  try {
    await logoutUser();
  } catch (e) {
    console.warn('로그아웃 요청 실패:', e);
  }
  localStorage.removeItem('accessToken');
  localStorage.removeItem('accessTokenTime');

  resetAccessToken();
  resetMatchId();
  resetSelectedMenu();
  window.location.replace('/login');
};
