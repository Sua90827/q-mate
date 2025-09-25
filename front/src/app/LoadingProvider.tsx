'use client';
import React, { useEffect, useState } from 'react';
import MainLoading from '../components/common/MainLoading';

export default function LoadingProvider({ children }: { children: React.ReactNode }) {
  const [show, setShow] = useState(true);

  useEffect(() => {
    const timer = setTimeout(() => {
      setShow(false);
    }, 1000);
    return () => clearTimeout(timer);
  }, []);

  return (
    <>
      {show && <MainLoading />}
      {children}
    </>
  );
}
