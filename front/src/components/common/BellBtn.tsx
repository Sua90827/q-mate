'use client';
import { Bell, Loader2 } from 'lucide-react';
import React, { useEffect, useRef, useState } from 'react';
import { Sheet, SheetContent, SheetTitle, SheetTrigger } from '../ui/sheet';
import {
  useInfiniteNotifications,
  useNotificationDetail,
  useUnreadCount,
} from '@/hooks/useNotificationList';
import { Skeleton } from '../ui/skeleton';
import { contentItemType } from '@/types/notification';
import { cn } from '@/lib/utils';
import { useRouter } from 'next/navigation';
import { useIntersectionObserver, useMediaQuery } from 'usehooks-ts';
import { flatNotifications, formatTimeAgo } from '@/utils/notificationUtils';
import CategoryIcons from '../notification/ui/CategoryIcons';

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
  }, [entry?.isIntersecting, hasNextPage, isFetchingNextPage, fetchNextPage]);

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
  return (
    <Sheet modal={false}>
      <SheetTrigger asChild>
        <div className="relative flex mr-7 w-fit h-full hover:opacity-80 rounded-md p-2 bell-btn justify-center items-center">
          <Bell className="w-8 h-8" />
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

      <SheetContent side="right" className="w-80 h-full">
        <SheetTitle className="h-10"></SheetTitle>
        <div ref={scrollRef} className="h-full overflow-y-auto">
          <ul className="flex flex-col gap-5 items-center">
            {items.map((item: contentItemType) => (
              <li
                key={item.notificationId}
                onClick={() => setSelectedId(item.notificationId)}
                className={cn(
                  `mx-3 p-3 flex items-center gap-4 ${
                    item.read === false ? 'bg-unread' : 'bg-read border-read-border border'
                  } w-[250px] h-25 rounded-sm cursor-pointer`,
                )}
              >
                <div className="flex flex-col justify-center">
                  <div className="flex gap-3">
                    <CategoryIcons
                      category={item.category}
                      className={cn(`w-6 h-6 ${item.read === false ? '!text-primary' : ''}`)}
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
