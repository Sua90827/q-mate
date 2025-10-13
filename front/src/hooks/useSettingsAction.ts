'use client';

import { useUpdateMatchInfo, useDisconnectMatch, useRestoreMatch } from '@/hooks/useMatches';
import { ErrorToast, SuccessToast } from '@/components/common/CustomToast';

export function useSettingsActions(matchId: number, closeModal: () => void) {
  const { mutateAsync: updateMatchInfo, isPending: isSavingTime } = useUpdateMatchInfo(matchId);
  const { mutateAsync: disconnectMatch, isPending: isDisconnecting } = useDisconnectMatch(matchId);
  const { mutateAsync: restoreMatch, isPending: isRestoring } = useRestoreMatch(matchId);

  const handleSaveTime = async (hour24: number) => {
    try {
      await updateMatchInfo({ dailyQuestionHour: hour24 });
      SuccessToast('질문 시간 저장 완료되었습니다.');
      closeModal();
    } catch {
      ErrorToast('질문 시간 저장에 실패했습니다');
    }
  };

  const handleDisconnect = async () => {
    try {
      const res = await disconnectMatch();
      SuccessToast(res?.message);
      closeModal();
    } catch {
      ErrorToast('연결 해제에 실패했습니다');
    }
  };

  const handleRestore = async () => {
    try {
      const res = await restoreMatch();
      SuccessToast(res?.message);
      closeModal();
    } catch {
      ErrorToast('연결 복구에 실패했습니다');
    }
  };

  return {
    handleSaveTime,
    handleDisconnect,
    handleRestore,

    loading: {
      isSavingTime,
      isDisconnecting,
      isRestoring,
    },
  };
}
