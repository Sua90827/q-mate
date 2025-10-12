import { customHandlers } from './handlers/custom';
import { inviteHandlers } from './handlers/invite';
import { authHandlers } from './handlers/login';
import { matchesHandlers } from './handlers/matches';
import { questionHandlers } from './handlers/questions';
import { signupHandlers } from './handlers/signup';
import { scheduleHandlers } from './handlers/schedule';
import { chartHandlers } from './handlers/chart';
import { notificationsHandler } from './handlers/notifications';
import { petHandlers } from './handlers/pet';

export const handlers = [
  ...questionHandlers,
  ...inviteHandlers,
  ...customHandlers,
  // ...matchesHandlers,
  // ...authHandlers,
  // ...signupHandlers,
  ...scheduleHandlers,
  ...chartHandlers,
  ...notificationsHandler,
  ...petHandlers,
];
