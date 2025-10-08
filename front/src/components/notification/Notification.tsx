'use client';
import { ChevronLeft, Loader2, Trash2 } from 'lucide-react';
import React, { useEffect, useRef, useState } from 'react';

import { useInfiniteNotifications, useNotificationDetail } from '@/hooks/useNotificationList';
import { Skeleton } from '../ui/skeleton';
import { contentItemType } from '@/types/notification';
import { cn } from '@/lib/utils';
import { useRouter } from 'next/navigation';
import { useIntersectionObserver } from 'usehooks-ts';
import { flatNotifications, formatTimeAgo } from '@/utils/notificationUtils';
import CategoryIcons from './ui/CategoryIcons';

export default function Notification() {
  const router = useRouter();
  const [selectedId, setSelectedId] = useState<number | null>(null);

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

  return (
    <div className="w-full h-full flex flex-col justify-center items-center sm:pt-0 pt-[70px]">
      <div className="fixed top-0 left-0 right-0 flex items-center justify-between py-5 sm:hidden px-4">
        <button
          className="relative flex ml-7 w-fit h-full hover:opacity-80 rounded-md p-2 bell-btn justify-center items-center"
          onClick={() => router.back()}
        >
          <ChevronLeft className="w-7 h-7" />
        </button>
        <span className="absolute left-1/2 -translate-x-1/2 font-Gumi text-20 text-theme-primary">
          알림
        </span>
        <button
          className="relative flex mr-7 w-fit h-full hover:opacity-80 rounded-md p-2 bell-btn justify-center items-center"
          onClick={() => router.back()}
        >
          <Trash2 className="w-7 h-7" />
        </button>
      </div>
      <div ref={scrollRef} className="w-full h-full overflow-y-auto bg-white">
        <ul className="flex flex-col items-center">
          {status === 'pending' && (
            <>
              {[...Array(9)].map((_, index) => (
                <li
                  key={`skeleton-${index}`}
                  className="mx-3 p-3 flex items-center gap-4 bg-read animate-pulse border-read-border w-full h-25 cursor-pointer"
                >
                  <div className="flex flex-col justify-center w-full">
                    <div className="flex gap-3 items-center">
                      <Skeleton className="w-6 h-6 rounded-full" />
                      <div className="flex flex-col text-14 font-normal gap-2 w-full">
                        <Skeleton className="h-4 w-3/5 rounded-md animate-pulse bg-neutral-400" />
                        <Skeleton className="h-3 w-2/5 rounded-md animate-pulse bg-neutral-400" />
                      </div>
                    </div>
                  </div>
                </li>
              ))}
            </>
          )}
          {items.map((item: contentItemType) => (
            <li
              key={item.notificationId}
              onClick={() => setSelectedId(item.notificationId)}
              className={cn(
                `mx-3 p-3 flex items-center gap-4 ${
                  item.read === false ? 'bg-unread' : 'bg-read border-read-border border'
                } w-full h-25 cursor-pointer`,
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
            <li className="text-[11px] text-muted-foreground pb-10">마지막 알림이에요.</li>
          )}
        </ul>
      </div>
    </div>
  );
}
