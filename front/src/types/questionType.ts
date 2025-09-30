//질문리스트
export interface QuestionList {
  questionInstanceId: number | string;
  deliveredAt: string;
  status: 'COMPLETED' | 'PENDING' | 'EXPIRED' | 'EDITABLE' | 'LOCKED';
  text: string;
  completedAt: string;
}

//질문리스트 전체 배열
export interface QuestionResponse {
  content: QuestionList[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
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

//커스텀 질문 조회 컨텐츠
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
//커스텀 질문 조회
export interface CustomQuestionPage {
  content: CustomQuestion[];
  last: boolean;
  number: number; // 현재 페이지
  size: number; // 페이지 크기
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
