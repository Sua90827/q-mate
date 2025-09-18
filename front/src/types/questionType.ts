export interface Answer {
  answerId: number;
  userId: number;
  isMine: boolean;
  visible: boolean;
  content: string | null;
  submittedAt: string;
}

export interface Question {
  questionId: number;
  sourceType?: 'ADMIN' | 'USER';
  relationType?: 'COUPLE' | 'FRIEND';
  text: string;
}

export interface QuestionDetail {
  questionInstanceId: number;
  matchId?: number;
  deliveredAt: string;
  status: 'PENDING' | 'COMPLETED' | 'CUSTOM';
  completedAt: string | null;
  question: Question;
  answers: Answer[];
}

export interface QuestionInstance {
  questionInstanceId: number;
  deliveredAt: string;
  status: string;
  question: Question;
  completedAt: string;
}

export interface CustomQuestion {
  questionId: number;
  sourceType: string;
  relationType: string;
  text: string;
  isActive: boolean;
  isEditable: boolean;
  createdAt: string;
}

export interface TodayQuestion {
  questionInstanceId: number;
  matchId: number;
  deliveredAt: string;
  status: 'PENDING' | 'COMPLETED' | 'EXPIRED';
  question: Question;
  myAnswer: string | null;
  partnerAnswer: string | null;
}
