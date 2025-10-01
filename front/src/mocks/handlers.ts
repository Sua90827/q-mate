import { customHandlers } from './handlers/custom';
import { inviteHandlers } from './handlers/invite';
import { authHandlers } from './handlers/login';
import { matchesHandlers } from './handlers/matches';
import { questionHandlers } from './handlers/questions';
import { scheduleHandlers } from './handlers/schedule';

export const handlers = [
  ...questionHandlers,
  ...inviteHandlers,
  ...customHandlers,
  ...matchesHandlers,
  ...authHandlers,
  ...scheduleHandlers,
];
