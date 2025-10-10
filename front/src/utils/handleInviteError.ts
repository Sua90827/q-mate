import { ModalConfig } from '@/types/modal';
import axios from 'axios';
import { Dispatch, SetStateAction } from 'react';

export const handleInviteError = (
  error: unknown,
  setModal: Dispatch<SetStateAction<ModalConfig>>,
  errorConfig: Record<number, ModalConfig>,
  remainingSeconds?: number,
) => {
  if (axios.isAxiosError(error)) {
    const status = error.response?.status ?? 0;

    if (status === 403 && remainingSeconds && remainingSeconds < 24 * 60 * 60) {
      const hours = Math.floor(remainingSeconds / 3600);
      const minutes = Math.floor((remainingSeconds % 3600) / 60);

      setModal({
        open: true,
        type: 'errorNotice',
        title:
          hours > 0
            ? `${hours}시간 ${minutes}분 후 다시 시도할 수 있습니다.`
            : `${minutes}분 후 다시 시도할 수 있습니다.`,
        isDanger: true,
      });
      return;
    }

    setModal(
      errorConfig[status] ?? {
        open: true,
        type: 'errorNotice',
        title: '예상치 못한 오류가 발생했습니다',
        sub: '잠시 후 다시 시도해 주세요.',
      },
    );
  } else {
    setModal({
      open: true,
      type: 'errorNotice',
      title: '예상치 못한 오류가 발생했습니다.',
      sub: '잠시 후 다시 시도해 주세요.',
    });
  }
};
