/** @type {import('next').NextConfig} */
module.exports = {
  reactStrictMode: true,
  output: 'export',
  images: {
    unoptimized: true,
  },
  basePath: '/mrt-buddy-web',
  assetPrefix: '/mrt-buddy-web',
  trailingSlash: true
}
