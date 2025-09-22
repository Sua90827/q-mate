'use client';
import React, { useState } from 'react';
import BellBtn from '../common/BellBtn';
import { ChevronRight } from 'lucide-react';
import { Switch } from '../ui/switch';
import { Button } from '../common/Button';
import NicknameModal from './ui/NicknameModal';
import { useThemeStore } from '@/store/useThemeStore';

type SettingItem =
  | { id: string; label: string; subLabel?: string; type: 'modal'; onClick: () => void }
  | { id: string; label: string; type: 'switch' };

export default function Settings() {
  const [modal, setModal] = useState<string | null>(null);
  const theme = useThemeStore((state) => state.theme);

  let colorClass = '';
  if (theme === 'sunset') colorClass = 'bg-sunset-active';
  else if (theme === 'night') colorClass = 'bg-night-active';
  else colorClass = 'bg-primary';

  const settings: SettingItem[] = [
    {
      id: 'profile',
      label: '조용한 유령',
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
      label: '연결 끊기',
      type: 'modal',
      onClick: () => setModal('disconnect'),
    },
  ];

  let whiteClass = '';
  if (theme === 'night') whiteClass = 'text-secondary';

  return (
    <div className="w-full min-h-screen flex flex-col justify-center items-center sm:pt-0 pt-[70px]">
      {/* 모바일 상단바 */}
      <div className="fixed top-0 left-0 right-0 flex items-center justify-between py-5 sm:hidden px-4 ">
        <div className="w-6" />
        <span className={`absolute left-1/2 -translate-x-1/2 font-Gumi text-20  ${whiteClass}`}>
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
              className="flex justify-between items-center px-4 py-5 cursor-pointer"
              onClick={item.type === 'modal' ? item.onClick : undefined}
            >
              <div>
                <span className="block text-16">{item.label}</span>
                {'subLabel' in item && item.subLabel && (
                  <span className="text-text-secondary font-normal text-12">{item.subLabel}</span>
                )}
              </div>
              {item.type === 'switch' ? (
                <Switch color={colorClass} />
              ) : (
                <ChevronRight className="text-text-secondary !w-4 !h-4" />
              )}
            </li>
          ))}
        </ul>
      </div>
      <Button className="w-[295px] mt-10" theme={theme}>
        로그아웃
      </Button>

      {modal === 'profile' && <NicknameModal open={modal} setIsOpen={setModal} />}
    </div>
  );
}
