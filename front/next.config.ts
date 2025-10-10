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
      // 일반 API 요청
      {
        source: '/api/:path*',
        destination: `${process.env.BACKEND_ORIGIN}/api/:path*`,
      },
      // 소셜 로그인 관련
      {
        source: '/oauth2/:path*',
        destination: `${process.env.BACKEND_ORIGIN}/oauth2/:path*`,
      },
    ];
  },
};

export default nextConfig;
