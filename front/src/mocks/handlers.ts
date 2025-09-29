import { customHandlers } from './handlers/custom';
import { inviteHandlers } from './handlers/invite';
import { matchesHandlers } from './handlers/matches';
import { questionHandlers } from './handlers/questions';

export const handlers = [
  ...questionHandlers,
  ...inviteHandlers,
  ...customHandlers,
  ...matchesHandlers,
];
