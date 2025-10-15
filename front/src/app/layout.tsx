import type { Metadata } from 'next';
import '../styles/globals.css';
import Providers from './providers';
import BodyWrapper from './BodyWrapper';
import NavGuard from '@/components/common/NavGuard';
import LoadingProvider from '@/app/LoadingProvider';
import Mocker from './Mocker';
import { cookies } from 'next/headers';
import { Toaster } from '@/components/ui/sonner';
import ServiceWorkerRegister from './ServiceWorker';
import ClientPushToast from '@/components/common/ClientPushToast';
import AuthGuard from '@/components/common/AuthGuard';

export const metadata: Metadata = {
  title: 'Q-mate',
  description: '매일의 질문으로 관계를 기록하는 친구·커플 전용 서비스',
  icons: {
    icon: '/favicon1.svg',
  },
};

export default async function RootLayout({ children }: { children: React.ReactNode }) {
  const cookieStore = await cookies();
  const theme = cookieStore.get('theme')?.value;

  return (
    <html lang="ko" className="h-full" data-theme={theme}>
      <body className="h-full">
        <Toaster position="top-center" offset={100} visibleToasts={1} />
        <BodyWrapper>
          <LoadingProvider>
            <Providers>
              <div className="flex flex-col h-full">
                <AuthGuard>
                  <NavGuard />
                  <main className="h-full flex-1 pt-0 sm:pt-[70px] pb-[70px] sm:pb-0">
                    <Mocker>{children}</Mocker>
                  </main>
                  <ClientPushToast />
                </AuthGuard>
              </div>
              <ServiceWorkerRegister />
            </Providers>
          </LoadingProvider>
        </BodyWrapper>
      </body>
    </html>
  );
}
