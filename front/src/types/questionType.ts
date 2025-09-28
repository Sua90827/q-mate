//질문리스트
export interface QuestionList {
  questionInstanceId: number;
  deliveredAt: string;
  status: 'COMPLETED' | 'PENDING' | 'EXPIRED' | 'EDITABLE';
  question: {
    questionId: number;
    text: string;
  };
  completedAt: string;
}

//질문리스트 전체 배열
export interface QuestionResponse {
  size: number; // 페이지당 요소 수
  content: QuestionList[];
}

// 답변 하나의 구조
export interface Answer {
  answerId: number;
  userId: number;
  nickname: string;
  visible: boolean;
  content: string;
  submittedAt: string;
  mine: boolean;
  isMine: boolean;
}

// 질문 정보
interface Question {
  questionId: number;
  sourceType: 'ADMIN' | 'CUSTOM';
  relationType: string;
  category: {
    id: number;
    name: string;
  };
  text: string;
}

// 질문 인스턴스 + 답변 목록
export interface AnswerResponseItem {
  questionInstanceId: number;
  matchId: number;
  deliveredAt: string;
  status: 'COMPLETED' | 'PENDING';
  completedAt: string;
  question: Question;
  answers: Answer[];
}

export interface CustomQuestion {
  customQuestionId: number;
  sourceType: 'CUSTOM';
  relationType: 'COUPLE' | 'FRIEND' | 'BOTH';
  matchId: number;
  text: string;
  createdAt: string;
  updatedAt: string;
  isEditable: boolean;
}

export interface TodayQuestion {
  questionInstanceId: number;
  matchId: number;
  deliveredAt: string;
  status: 'PENDING' | 'COMPLETED' | 'EXPIRED';
  completedAt: string | null;
  question: Question;
  answers: Answer[];
}
