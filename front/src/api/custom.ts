import { CustomQuestionPage } from '@/types/questionType';
import instance from '../lib/axiosInstance';

// 커스텀 질문 조회
export const fetchCustomQuestions = async (
  matchId: number,
  page: number = 0,
  size: number = 20,
): Promise<CustomQuestionPage> => {
  const res = await instance.get(`api/matches/${matchId}/custom-questions`, {
    params: {
      page,
      size,
      status: 'EDITABLE',
    },
  });
  return res.data;
};

// 커스텀 질문 등록
export const createCustomQuestion = async ({
  text,
  matchId,
}: {
  text: string;
  matchId: number;
}) => {
  const res = await instance.post(`api/matches/${matchId}/custom-questions`, { text });
  return res.data;
};

// 커스텀 질문 수정
export const updateCustomQuestion = async ({ text, id }: { text: string; id: number }) => {
  const res = await instance.patch(`api/custom-questions/${id}`, { text });
  return res.data;
};

// 커스텀 질문 삭제
export const deleteCustomQuestion = async (id: number) => {
  const res = await instance.delete(`api/custom-questions/${id}`);
  return res.data;
};
