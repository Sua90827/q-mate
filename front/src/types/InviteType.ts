export interface Invited {
  matchId: number;
  message: string;
  partnerNickname: string;
}

export interface LockStatus {
  remainingSeconds: number;
  locked: boolean;
}
