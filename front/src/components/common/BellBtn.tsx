'use client';
import { Bell, Loader2, X } from 'lucide-react';
import React, { useEffect, useRef, useState } from 'react';
import { Sheet, SheetContent, SheetTitle, SheetTrigger } from '../ui/sheet';
import {
  useInfiniteNotifications,
  useNotificationDetail,
  useUnreadCount,
} from '@/hooks/useNotificationList';
import { contentItemType } from '@/types/notification';
import { cn } from '@/lib/utils';
import { useRouter } from 'next/navigation';
import { useIntersectionObserver, useMediaQuery } from 'usehooks-ts';
import { flatNotifications, formatTimeAgo } from '@/utils/notificationUtils';
import CategoryIcons from '../notification/ui/CategoryIcons';
import { deleteNotification } from '@/api/notification';

export default function BellBtn() {
  const isMobile = useMediaQuery('(max-width: 640px)');

  const { data: unreadData } = useUnreadCount();

  const router = useRouter();
  const [selectedId, setSelectedId] = useState<number | null>(null);
  const unread = unreadData?.count ?? 0;
  const { data: detail, isLoading } = useNotificationDetail(selectedId ?? undefined);

  const {
    data: infinity,
    fetchNextPage,
    hasNextPage,
    isFetchingNextPage,
    status,
  } = useInfiniteNotifications({ size: 20 });

  const items: contentItemType[] = flatNotifications(infinity);
  const scrollRef = useRef<HTMLDivElement | null>(null);

  const { ref: sentinelRef, entry } = useIntersectionObserver({
    root: scrollRef.current,
    rootMargin: '0px 0px 40px 0px',
    threshold: 0,
  });

  useEffect(() => {
    if (entry?.isIntersecting && hasNextPage && !isFetchingNextPage) {
      fetchNextPage();
    }
    if (!selectedId) return;
    if (isLoading || !detail) return;
  }, [
    entry?.isIntersecting,
    hasNextPage,
    isFetchingNextPage,
    fetchNextPage,
    selectedId,
    isLoading,
    detail,
    router,
  ]);

  if (isMobile) {
    return (
      <button
        className="relative flex mr-7 w-fit h-full hover:opacity-80 rounded-md p-2 bell-btn justify-center items-center"
        onClick={() => router.push('/notification')}
      >
        <Bell className="w-7 h-7" />
        {unread > 0 && (
          <span className="absolute top-1 right-1 w-4 h-4 bg-red-500 text-white rounded-full text-xs flex items-center justify-center">
            {unread}
          </span>
        )}
      </button>
    );
  }
  const clickHandler = async (item: contentItemType): Promise<void> => {
    setSelectedId(item.notificationId);

    let href = '/';
    switch (item.category) {
      case 'EVENT':
        href = '/schedule';
        break;
      case 'QUESTION':
        href = `/question/detail?id=${detail?.resourceId}`;
        break;
      case 'MATCH':
        href = '/main';
        break;
    }

    try {
      await router.prefetch(href);
    } catch (e) {
      console.error(e);
    }
    router.push(href);
  };

  return (
    <Sheet modal={false}>
      <SheetTrigger asChild>
        <div className="relative flex mr-7 w-fit h-full hover:opacity-80 rounded-md p-2 bell-btn justify-center items-center">
          <Bell className="!w-8 !h-8" />
          {unread > 0 && (
            <div
              aria-label={`읽지 않은 알림 ${unread}개`}
              className="absolute top-1 right-1 flex items-center justify-center w-5 h-5 rounded-full bg-red-500 text-white text-xs font-bold"
            >
              {unread}
            </div>
          )}
        </div>
      </SheetTrigger>

      <SheetContent side="right" className="w-100 h-full">
        <SheetTitle className="h-10"></SheetTitle>
        <div ref={scrollRef} className="h-full overflow-y-auto">
          <ul className="flex flex-col gap-5 items-center">
            {items.map((item: contentItemType) => (
              <li
                key={item.notificationId}
                className={cn(
                  `mx-3 p-3 flex items-center gap-4 ${
                    item.read === false ? 'bg-unread' : 'bg-read border-read-border border'
                  } w-[290px] h-25 rounded-sm`,
                )}
              >
                <div className="flex justify-between w-full h-full items-center">
                  <div
                    className="flex gap-3 w-50 h-full py-3 cursor-pointer"
                    onClick={() => clickHandler(item)}
                  >
                    <CategoryIcons
                      category={item.category}
                      className={cn(
                        `w-6 h-6 items-start mt-1 ${
                          item.read === false ? '!text-primary' : '!text-text-unread'
                        } `,
                      )}
                    />
                    <div className="flex flex-col text-14 font-normal">
                      <div
                        className={cn(
                          `flex text-16 font-bold items-center gap-2 ${
                            item.read === false ? '!text-text-primary' : '!text-text-unread'
                          }`,
                        )}
                      >
                        {item.listTitle}
                        {item.read === false && (
                          <span className="w-2 h-2 bg-primary rounded-full"></span>
                        )}
                      </div>

                      <p className="text-text-unread">{formatTimeAgo(item.createdAt)}</p>
                    </div>
                  </div>
                  <div
                    className="flex h-full w-10 items-center"
                    onClick={() => deleteNotification(item.notificationId)}
                  >
                    <X className="!w-5 !h-5 !text-text-secondary " />
                  </div>
                </div>
              </li>
            ))}
            <div ref={sentinelRef} className="h-6" />
            {isFetchingNextPage && (
              <li className="pb-3">
                <Loader2 className="h-4 w-4 animate-spin" />
              </li>
            )}
            {!hasNextPage && items.length > 0 && (
              <li className="text-[11px] text-muted-foreground pb-3">마지막 알림이에요.</li>
            )}
          </ul>
        </div>
      </SheetContent>
    </Sheet>
  );
}
