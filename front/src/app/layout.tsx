import type { Metadata } from 'next';
import '../styles/globals.css';
import Providers from './providers';
import NavGuard from '@/components/common/NavGuard';
import BodyWrapper from './BodyWrapper';

export const metadata: Metadata = {
  title: 'Q-mate',
  description: '매일의 질문으로 관계를 기록하는 친구·커플 전용 서비스',
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
    <html lang="ko">
      <body>
        <BodyWrapper>
          <div className="order-last sm:order-1 z-10">
            <NavGuard />
          </div>
          <Providers>
            <main className="flex-1 order-1 sm:order-last">{children}</main>
          </Providers>
        </BodyWrapper>
      </body>
    </html>
  );
}
