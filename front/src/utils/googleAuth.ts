export function buildGoogleAuthUrl() {
  const base = 'https://accounts.google.com/o/oauth2/v2/auth';
  const params = new URLSearchParams({
    client_id: process.env.NEXT_PUBLIC_GOOGLE_CLIENT_ID!,
    response_type: 'code',
    scope: 'openid email profile',
    redirect_uri: process.env.NEXT_PUBLIC_GOOGLE_REDIRECT_URI!,
    state: cryptoRandom(), // 선택: CSRF 방지(세션/스토리지에 저장)
    nonce: cryptoRandom(), // 선택: id_token nonce 대조용
  });
  // state/nonce 저장
  if (typeof window !== 'undefined') {
    localStorage.setItem('google_state', params.get('state')!);
    localStorage.setItem('google_nonce', params.get('nonce')!);
  }
  return `${base}?${params.toString()}`;
}

function cryptoRandom() {
  if (typeof window === 'undefined') return '';
  const buf = new Uint8Array(16);
  window.crypto.getRandomValues(buf);
  return Array.from(buf)
    .map((b) => b.toString(16).padStart(2, '0'))
    .join('');
}
