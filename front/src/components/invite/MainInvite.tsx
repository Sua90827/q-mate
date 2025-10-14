'use client';
import React, { useEffect, useState } from 'react';
import { Button } from '../common/Button';
import Link from 'next/link';
import { useRestorableMatchId, useRestoreMatch } from '@/hooks/useMatches';
import { ErrorToast, SuccessToast } from '../common/CustomToast';
import ConfirmModal from '../common/ConfirmModal';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useRouter } from 'next/navigation';

export default function MainInvite() {
  const router = useRouter();
  const [restoreOpen, setRestoreOpen] = useState(false);
  const [id, setId] = useState<number | null>(null);
  const setMatchId = useMatchIdStore((state) => state.setMatchId);
  const { mutateAsync: checkRestoreStatus } = useRestorableMatchId();
  const { mutate: restoreMatch } = useRestoreMatch();
  useEffect(() => {
    let mounted = true;
    (async () => {
      try {
        const res = await checkRestoreStatus();

        if (!mounted) return;
        if (res.hasDetachedMatch && res.matchId) {
          setRestoreOpen(true);
          setId(res.matchId);
        }
      } catch {
        ErrorToast('매칭 복구 가능 여부 확인에 실패했습니다.');
      }
    })();
    return () => {
      mounted = false;
    };
  }, [checkRestoreStatus]);
  const confirmHandler = async () => {
    if (!id) {
      ErrorToast('잠시 후 다시 시도해 주세요.');
      return;
    }
    restoreMatch(id, {
      onSuccess: (res) => {
        SuccessToast(res?.message);
        setMatchId(id);
        setRestoreOpen(false);
        router.push('/main');
      },
      onError: () => {
        ErrorToast('매칭 복구에 실패했어요. 잠시 후 다시 시도해 주세요.');
      },
    });
  };
  return (
    <>
      <Button variant="invite" className="w-[300px] z-10" asChild>
        <Link href="invite/relationship_select">초대 하기</Link>
      </Button>
      <Button variant="primary" className="w-[300px] z-10" asChild>
        <Link href="/invite/invited">초대 코드 입력하기</Link>
      </Button>
      <ConfirmModal
        open={restoreOpen}
        setOpen={setRestoreOpen}
        onConfirm={() => {
          confirmHandler();
        }}
        title="복구 가능한 매칭이 있어요"
        sub="이전에 연결된 매칭을 복구하시겠어요?"
      />
    </>
  );
}
