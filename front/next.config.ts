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
        destination: `${process.env.BACKEND_ORIGIN}/:path*`,
      },
    ];
  },
};

export default nextConfig;
