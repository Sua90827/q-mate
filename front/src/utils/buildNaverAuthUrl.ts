import { createState } from './naveState';

export function buildNaverAuthorizeUrl(): string {
  const state = createState('naver');

  const clientId = process.env.NEXT_PUBLIC_NAVER_CLIENT_ID!;
  const redirectUri = process.env.NEXT_PUBLIC_NAVER_REDIRECT_URI!;
  const authorizeUrl =
    process.env.NEXT_PUBLIC_NAVER_AUTHORIZE_URL || 'https://nid.naver.com/oauth2.0/authorize';
  const scope = process.env.NEXT_PUBLIC_NAVER_SCOPE?.split(',').map((s) => s.trim()) ?? [
    'profile',
    'email',
    'birthday',
    'birthyear',
  ];

  const url = new URL(authorizeUrl);
  url.searchParams.set('response_type', 'code');
  url.searchParams.set('client_id', clientId);
  url.searchParams.set('redirect_uri', redirectUri);
  url.searchParams.set('state', state);
  url.searchParams.set('scope', scope.join(' '));
  //  자동 로그인 방지
  url.searchParams.set('auth_type', 'reprompt');
  return url.toString();
}
