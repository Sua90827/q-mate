export function createState(prefix: string = 'naver'): string {
  const random = crypto.getRandomValues(new Uint8Array(16));
  const base64 = btoa(String.fromCharCode(...random)).replace(/=|\+|\//g, '');
  const state = `${prefix}.${Date.now()}.${base64}`;
  sessionStorage.setItem(`${prefix}_state`, state);
  return state;
}

export function verifyState(prefix: string, incoming: string | null): boolean {
  const saved = sessionStorage.getItem(`${prefix}_state`);
  sessionStorage.removeItem(`${prefix}_state`);
  return !!incoming && !!saved && incoming === saved;
}
