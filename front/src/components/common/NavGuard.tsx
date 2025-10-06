'use client';

import { usePathname } from 'next/navigation';
import React from 'react';
import Nav from './Nav';

export default function NavGuard() {
  const pathName = usePathname();
  const hideNav =
    pathName === '/' ||
    pathName.startsWith('/login') ||
    pathName.startsWith('/signup') ||
    pathName.startsWith('/invite');

  if (hideNav) return null;
  return <Nav />;
}
