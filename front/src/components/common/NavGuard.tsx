'use client';

import { usePathname } from 'next/navigation';
import React from 'react';
import Nav from './Nav';

export default function NavGuard() {
  const pathName = usePathname();
  const cleanPath = pathName.replace(/\/$/, '');
  const hideNav =
    cleanPath.startsWith('/login') ||
    cleanPath.startsWith('/signup') ||
    cleanPath.startsWith('/invite');

  if (hideNav) return null;
  return <Nav />;
}
