export interface MatchUser {
  userId: number;
  nickname: string;
}

export interface MatchInfo {
  matchId: number;
  relationType: 'FRIEND' | 'COUPLE';
  startDate: string | null;
  dailyQuestionHour: number;
  status: 'Active' | 'DETACHED_PENDING_DELETE';
  users: MatchUser[];
}
