export interface MatchUser {
  userId: number;
  nickname: string;
  me: boolean;
  agreed: boolean;
}

export interface MatchInfo {
  matchId: number;
  relationType: 'FRIEND' | 'COUPLE';
  startDate: string | null;
  dailyQuestionHour: number;
  status: 'WAITING' | 'ACTIVE';
  users: MatchUser[];
}
