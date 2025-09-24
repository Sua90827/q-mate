import type { Metadata } from 'next';
import '../styles/globals.css';
import Providers from './providers';
import BodyWrapper from './BodyWrapper';
import NavGuard from '@/components/common/NavGuard';

export const metadata: Metadata = {
  title: 'Q-mate',
  description: '매일의 질문으로 관계를 기록하는 친구·커플 전용 서비스',
  icons: {
    icon: '/favicon.svg',
  },
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="ko" className="h-full" suppressHydrationWarning>
      <head>
        <script
          dangerouslySetInnerHTML={{
            //테마 시간 설정
            __html: `
              (function() {
                var hour = new Date().getHours();
                var theme = (hour >= 6 && hour < 18) ? "day"
                          : (hour >= 18 && hour < 21) ? "night"
                          : "night";
                document.documentElement.setAttribute("data-theme", theme);
              })();
            `,
          }}
        />
      </head>
      <body className="h-full">
        <BodyWrapper>
          <Providers>
            <div className="flex flex-col h-full">
              <NavGuard />
              <main className="h-full flex-1 pt-0 sm:pt-[70px] pb-[70px] sm:pb-0">{children}</main>
            </div>
          </Providers>
        </BodyWrapper>
      </body>
    </html>
  );
}
