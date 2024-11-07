/** @type {import('next').NextConfig} */
module.exports = {
  reactStrictMode: true,
  output: 'export',
  images: {
    unoptimized: true,
  },
  basePath: process.env.GITHUB_ACTIONS ? '/mrt-buddy-web' : '',
  assetPrefix: process.env.GITHUB_ACTIONS ? '/mrt-buddy-web/' : '',
  trailingSlash: true,
}
