import '../styles/globals.css'
import { Noto_Sans } from 'next/font/google'

const notoSans = Noto_Sans({
  subsets: ['latin'],
  weight: ['400', '500', '600', '700'],
  display: 'swap',
})

export default function App({ Component, pageProps }) {
  return (
    <main className={notoSans.className}>
      <Component {...pageProps} />
    </main>
  )
}
