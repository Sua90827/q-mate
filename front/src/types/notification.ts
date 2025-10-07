export type categoryType = 'QUESTION' | 'EVENT' | 'MATCH';
export type codeType =
  | 'QI_TODAY_READY'
  | 'QI_REMINDER'
  | 'QI_COMPLETED'
  | 'EVENT_SAME_DAY'
  | 'EVENT_THREE_DAY_BEFORE'
  | 'EVENT_WEEK_BEFORE';
export type resourceType = 'QUESTION_INSTANCE' | 'EVENT' | 'MATCH' | 'NONE';
export type sortType = { sorted: boolean; empty: boolean; unsorted: boolean };
export type contentItemType = {
  notificationId: number;
  category: categoryType;
  code: codeType;
  listTitle: string;
  createdAt: string;
  read: boolean;
};
//알림상세조회용type
export interface notificationResponseType {
  notificationId: number;
  userId: number;
  matchId: number;
  category: categoryType;
  code: codeType;
  listTitle: string;
  pushTitle: string;
  resourceType: resourceType;
  resourceId: number;
  readAt: string | null;
  createdAt: string;
}

export interface notificationListResponseType {
  totalElements: number;
  totalPages: number;
  pageable: {
    paged: boolean;
    pageNumber: number;
    pageSize: number;
    offset: number;
    sort: sortType;
    unpaged: boolean;
  };
  size: number;
  content: contentItemType[];

  number: number;
  sort: sortType;
  first: boolean;
  last: boolean;
  numberOfElements: number;
  empty: boolean;
}

//푸시 알림 구독관련 type
export interface subScriptionsType {
  endpoint: string;
  keyP256dh: string;
  keyAuth: string;
  keys: {
    p256dh: string;
    auth: string;
  };
}
export interface vapidPublicKeyType {
  vapidPublicKey: string;
}
export type ListParams = {
  category?: categoryType;
  code?: codeType;
  unread?: boolean;
  page?: number;
  size?: number;
  sort?: sortType;
};
