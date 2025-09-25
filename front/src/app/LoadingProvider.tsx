'use client';
import React, { useEffect, useState } from 'react';
import MainLoading from '../components/common/MainLoading';
import { AnimatePresence, motion } from 'motion/react';
import { usePathname } from 'next/navigation';

export default function LoadingProvider({ children }: { children: React.ReactNode }) {
  const [show, setShow] = useState(true);
  const pathname = usePathname();

  useEffect(() => {
    const timer = setTimeout(() => {
      setShow(false);
    }, 1000);
    return () => clearTimeout(timer);
  }, []);

  return (
    <>
      <AnimatePresence>
        {show && pathname === '/' && (
          <motion.div initial={{ opacity: 1 }} exit={{ opacity: 0 }} transition={{ duration: 1 }}>
            <MainLoading />
          </motion.div>
        )}
      </AnimatePresence>
      {children}
    </>
  );
}
