/** @type {import('next').NextConfig} */
module.exports = {
  reactStrictMode: true,
  output: 'export',
  images: {
    unoptimized: true,
  },
<<<<<<< HEAD
  basePath: '/mrt-buddy-web',
  assetPrefix: '/mrt-buddy-web',
  trailingSlash: true
=======
  basePath: process.env.GITHUB_ACTIONS ? '/mrt-buddy-web' : '',
  assetPrefix: process.env.GITHUB_ACTIONS ? '/mrt-buddy-web/' : '',
  trailingSlash: true,
>>>>>>> upstream/gh-pages-new
}
