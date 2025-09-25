'use client';
import React, { useEffect, useState } from 'react';
import MainLoading from '../components/common/MainLoading';
import { AnimatePresence, motion } from 'motion/react';

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
      <AnimatePresence>
        {show && (
          <motion.div initial={{ opacity: 1 }} exit={{ opacity: 0 }} transition={{ duration: 1 }}>
            <MainLoading />
          </motion.div>
        )}
      </AnimatePresence>
      {children}
    </>
  );
}
