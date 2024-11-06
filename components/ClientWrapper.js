import React from 'react';
import dynamic from 'next/dynamic';

const ClientWrapper = ({ children }) => {
  return <>{children}</>;
};

export default dynamic(() => Promise.resolve(ClientWrapper), {
  ssr: false
});
