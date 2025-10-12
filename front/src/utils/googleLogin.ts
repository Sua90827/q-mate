export const googleLogin = () => {
  const frontendUrl = process.env.NEXT_PUBLIC_FRONTEND_ORIGIN;
  const clientId = process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID;

  if (!frontendUrl || !clientId) {
    return;
  }

  // 리다이렉트 URI
  const redirectUri = `${frontendUrl}/login/oauth2/code/google`;

  // 로그인 URL 생성
  const authUrl = new URL('https://accounts.google.com/o/oauth2/v2/auth');
  authUrl.searchParams.set('client_id', clientId);
  authUrl.searchParams.set('redirect_uri', redirectUri);
  authUrl.searchParams.set('response_type', 'code');
  authUrl.searchParams.set('scope', 'email profile openid');
  authUrl.searchParams.set('access_type', 'offline');

  // 페이지 이동 (전체 리다이렉트)
  window.location.href = authUrl.toString();
};
