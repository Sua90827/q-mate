import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Q-mate',
  description: '친구 또는 커플을 위한 일일 질문 기반 기록',
  icons: {
    icon: '/favicon.svg',
  },
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
