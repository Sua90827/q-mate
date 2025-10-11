import type { NextConfig } from 'next';
import path from 'path';

const nextConfig: NextConfig = {
  webpack: (config) => {
    config.resolve.alias = {
      ...config.resolve.alias,
      '@': path.resolve(__dirname, 'src'),
    };
    return config;
  },
  async rewrites() {
    return [
      // 소셜 로그인 관련 요청 (백엔드 직접 호출)
      {
        source: '/oauth2/:path*',
        destination: `${process.env.NEXT_PUBLIC_BACKEND_ORIGIN}/oauth2/:path*`,
      },
      {
        source: '/auth/:path*',
        destination: `${process.env.NEXT_PUBLIC_BACKEND_ORIGIN}/auth/:path*`,
      },
      // 일반 API 요청
      {
        source: '/api/:path*',
        destination: `${process.env.NEXT_PUBLIC_BACKEND_ORIGIN}/api/:path*`,
      },
    ];
  },
};

export default nextConfig;
