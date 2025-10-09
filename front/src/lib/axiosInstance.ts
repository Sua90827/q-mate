import instance from '@/api/axiosInstance';
import { handleUnauthorized } from './redirectHandler';

instance.interceptors.response.use(
  (res) => res,
  (error) => {
    if (error.response?.status === 401) {
      handleUnauthorized();
      // router는 컴포넌트 내부 useEffect 등에서 감지해서 처리
    }
    return Promise.reject(error);
  },
);
