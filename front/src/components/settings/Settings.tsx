'use client';
import React, { useEffect, useState } from 'react';
import BellBtn from '../common/BellBtn';
import { ChevronRight, UserRoundPen } from 'lucide-react';
import { Switch } from '../ui/switch';
import { Button } from '../common/Button';
import NicknameModal from './ui/NicknameModal';
import { cn } from '@/lib/utils';
import QuestionTimeModal from './ui/QuestionTimeModal';
import { useMatchInfo } from '@/hooks/useMatches';
import { useSettingsActions } from '@/hooks/useSettingsAction';
import ConnectionModal from './ui/ConnectionModal';
import { useMatchIdStore } from '@/store/useMatchIdStore';
import { useNotificationSettings } from '@/hooks/useNotificationSettings';
import { useAuthStore } from '@/store/useAuthStore';
import { useLogoutUser } from '@/hooks/useAuth';
import { ErrorToast } from '../common/CustomToast';
import { useRouter } from 'next/navigation';
import ConfirmModal from '../common/ConfirmModal';
import { useUnsubscribePush } from '@/hooks/useUnSubScription';
import { useSelectedStore } from '@/store/useSelectedStore';

type SettingItem =
  | { id: string; label: string; subLabel?: string; type: 'modal'; onClick: () => void }
  | { id: string; label: string; type: 'switch' };

export default function Settings() {
  const matchId = useMatchIdStore((state) => state.matchId);
  const { data: matchInfo } = useMatchInfo(matchId!);
  const user = matchInfo?.users.find((u) => u.me);
  const resetMatchId = useMatchIdStore((state) => state.resetMatchId);
  const resetAccessToken = useAuthStore((state) => state.resetAccessToken);
  const resetSelectedMenu = useSelectedStore((state) => state.resetSelectedMenu);
  const [modal, setModal] = useState<string | null>(null);

  //hook 조회 enable에 사용자가 있을때 조건 추가 필요
  const { data: notificationSettings, toggleNotification, isPending } = useNotificationSettings();
  const { unsubscribe } = useUnsubscribePush();
  const [nickname, setNickname] = useState<string>('');
  const [isLogoutOpen, setIsLogoutOpen] = useState(false);

  const { mutate: logoutMutate, isPending: isLogoutPending } = useLogoutUser();
  const router = useRouter();

  useEffect(() => {
    if (user?.nickname) {
      setNickname(user.nickname);
    }
  }, [user]);

  const { handleSaveTime, handleDisconnect, handleRestore, loading } = useSettingsActions(
    matchId!,
    () => setModal(null),
  );

  if (!user) return;

  const settings: SettingItem[] = [
    {
      id: 'profile',
      label: nickname!,
      subLabel: '닉네임 수정하기',
      type: 'modal',
      onClick: () => setModal('profile'),
    },
    {
      id: 'notification',
      label: '알림 설정',
      type: 'switch',
    },
    {
      id: 'time',
      label: '질문 시간 설정',
      type: 'modal',
      onClick: () => setModal('time'),
    },
    {
      id: 'disconnect',
      label: matchInfo?.status === 'ACTIVE' ? '연결 끊기' : '연결 복구',
      type: 'modal',
      onClick: () => setModal('disconnect'),
    },
  ];

  const pushEnabled = notificationSettings?.pushEnabled;

  console.log('pushEnabled', pushEnabled);
  const handleTogglePush = (next: boolean) => {
    toggleNotification(next);
  };
  const handleLogout = () => {
    logoutMutate(undefined, {
      onSuccess: () => {
        sessionStorage.clear();
        //선택된 메뉴 리셋
        resetSelectedMenu();
        // exp 리셋
        localStorage.clear();
        // 매치 아이디 리셋
        resetMatchId();
        // 토큰 리셋
        resetAccessToken();
        //구독 해지
        unsubscribe();
        router.replace('/');
      },
      onError: () => {
        ErrorToast('로그아웃에 실패했습니다. 다시 시도해 주세요.');
      },
    });
  };

  return (
    <div className="w-full h-full flex flex-col justify-center items-center sm:pt-0 pt-[70px]">
      {/* 모바일 상단바 */}
      <div className="fixed top-0 left-0 right-0 flex items-center justify-between py-5 sm:hidden">
        <div className="w-6" />
        <span className="absolute left-1/2 -translate-x-1/2 font-Gumi text-20 text-theme-primary">
          설정
        </span>
        <BellBtn />
      </div>

      {/* 설정 리스트 */}
      <div className="w-[295px] shadow-box">
        <ul className="divide-y divide-gray">
          {settings.map((item) => (
            <li
              key={item.id}
              className={`flex justify-between items-center px-4 py-5 cursor-pointer ${
                item.id === 'profile' ? 'text-theme-accent2 font-extrabold' : ''
              }`}
              onClick={item.type === 'modal' ? item.onClick : undefined}
            >
              <div>
                <div className="flex gap-2">
                  {item.id === 'profile' ? <UserRoundPen className="w-5" /> : ''}
                  <span className="block text-16">{item.label}</span>
                </div>
                {'subLabel' in item && item.subLabel && (
                  <span className="text-theme-secondary font-normal text-12">{item.subLabel}</span>
                )}
              </div>
              {item.type === 'switch' ? (
                //현재는 useState로 색상변경되는지 확인했지만 유저 정보에서 알림을 받는지 끄는지 확인필요할듯
                <Switch
                  checked={pushEnabled}
                  onCheckedChange={handleTogglePush}
                  className={cn(pushEnabled && 'bg-theme-primary', 'cursor-pointer')}
                  disabled={isPending}
                />
              ) : (
                <ChevronRight className="text-theme-secondary !w-4 !h-4" />
              )}
            </li>
          ))}
        </ul>
      </div>

      <Button className="w-[295px] mt-10" onClick={() => setIsLogoutOpen(true)}>
        {isLogoutPending ? '로그아웃 중...' : '로그아웃'}
      </Button>

      {modal === 'profile' && (
        <NicknameModal
          open={modal}
          setIsOpen={setModal}
          nickname={nickname!}
          setNickname={setNickname}
        />
      )}
      <QuestionTimeModal
        open={modal === 'time'}
        setIsOpen={(open) => setModal(open ? 'time' : null)}
        initialHour={matchInfo?.dailyQuestionHour}
        onSave={handleSaveTime}
      />
      {matchInfo?.status !== undefined && (
        <ConnectionModal
          open={modal === 'disconnect'}
          setIsOpen={(open) => setModal(open ? 'disconnect' : null)}
          onClick={matchInfo.status === 'ACTIVE' ? handleDisconnect : handleRestore}
          status={matchInfo.status}
          loading={matchInfo.status === 'ACTIVE' ? loading.isDisconnecting : loading.isRestoring}
        />
      )}

      <ConfirmModal
        defaultStyle
        open={isLogoutOpen}
        setOpen={setIsLogoutOpen}
        onConfirm={handleLogout}
        title="정말 로그아웃 하시겠어요?"
        sub="로그아웃 시 시작 화면으로 돌아갑니다."
      />
    </div>
  );
}
