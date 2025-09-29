import type { Metadata } from 'next';
import '../styles/globals.css';
import Providers from './providers';
import BodyWrapper from './BodyWrapper';
import NavGuard from '@/components/common/NavGuard';
import LoadingProvider from '@/app/LoadingProvider';
import Mocker from './Mocker';
import { cookies } from 'next/headers';
import { Toaster } from '@/components/ui/sonner';

export const metadata: Metadata = {
  title: 'Q-mate',
  description: '매일의 질문으로 관계를 기록하는 친구·커플 전용 서비스',
  icons: {
    icon: '/favicon.svg',
  },
};

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  const cookieStore = await cookies();
  const theme = cookieStore.get('theme')?.value;

  return (
    <html lang="ko" className="h-full" data-theme={theme}>
      <body className="h-full">
        <BodyWrapper>
          <LoadingProvider>
            <Providers>
              <Mocker />
              <div className="flex flex-col h-full">
                <NavGuard />
                <main className="h-full flex-1 pt-0 sm:pt-[70px] pb-[70px] sm:pb-0">
                  {children}
                </main>
                <Toaster position="top-center" offset={100} />
              </div>
            </Providers>
          </LoadingProvider>
        </BodyWrapper>
      </body>
    </html>
  );
}
