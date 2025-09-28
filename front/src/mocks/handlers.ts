import { customHandlers } from './handlers/custom';
import { inviteHandlers } from './handlers/invite';
import { questionHandlers } from './handlers/questions';

export const handlers = [...questionHandlers, ...inviteHandlers, ...customHandlers];
