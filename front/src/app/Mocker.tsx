// src/app/Mocker.tsx
'use client';

import { useEffect } from 'react';

export default function Mocker() {
  useEffect(() => {
    if (process.env.NODE_ENV === 'development') {
      import('../mocks/browser').then(({ worker }) => {
        worker.start({
          // 선택: 콘솔 경고 줄이고 싶으면
          onUnhandledRequest: 'bypass',
        });
        console.info('[MSW] Mocking enabled.');
      });
    }
  }, []);

  return null;
}
