import axios from 'axios';
import { InvitedCheck } from '@/types/InviteType';

export const fetchInvitedCheck = async (): Promise<InvitedCheck> => {
  const res = await axios.get<InvitedCheck>('http://localhost:3001/invited');
  return res.data;
};
