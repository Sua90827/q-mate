'use client';
import { useSearchParams } from 'next/navigation';
import { useEffect, useState } from 'react';

export default function GoogleCallback() {
  const searchParams = useSearchParams();
  const [params, setParams] = useState<string>('');

  useEffect(() => {
    // 현재 URL의 전체 쿼리 파라미터를 보기 좋게 문자열로 정리
    const entries = Array.from(searchParams.entries());
    if (entries.length > 0) {
      const formatted = entries
        .map(([key, value]) => `${key} = ${decodeURIComponent(value)}`)
        .join('\n');
      setParams(formatted);
    } else {
      setParams('❌ URL에 전달된 code/state 파라미터가 없습니다.');
    }
  }, [searchParams]);

  return (
    <div style={{ padding: '2rem', whiteSpace: 'pre-wrap', fontFamily: 'monospace' }}>
      <h2>🔍 Google OAuth 콜백 파라미터</h2>
      <hr />
      <p>{params}</p>
    </div>
  );
}
