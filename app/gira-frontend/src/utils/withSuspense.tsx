import React, { Suspense } from 'react';
import { Spin } from 'antd';

export const withSuspense = (Component: React.ComponentType) => (
  <Suspense fallback={<Spin size="large" />}>
    <Component />
  </Suspense>
); 