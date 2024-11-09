import { GoogleAnalytics } from "@next/third-parties/google";
import { Noto_Sans } from "next/font/google";
import Head from "next/head";
import { useTheme } from "../contexts";
import ThemeProvider from "../providers/ThemeProvider";
import "../styles/globals.css";

const notoSans = Noto_Sans({
  subsets: ["latin"],
  weight: ["400", "500", "600", "700"],
  display: "swap",
});

export default function App({ Component, pageProps }) {
  return (
    <ThemeProvider>
      <MainContent Component={Component} pageProps={pageProps} />
    </ThemeProvider>
  );
}

function MainContent({ Component, pageProps }) {
  const { isDarkMode } = useTheme();
  return (
    <main className={`${notoSans.className} ${isDarkMode ? "dark" : ""}`}>
      <Head>
        <meta property="og:url" content="https://mrtbuddy.com/" />
        <meta property="og:type" content="website" />
        <meta
          property="og:title"
          content="MRT Buddy - Your Dhaka Metro Rail Companion App"
        />
        <meta
          property="og:description"
          content="MRT Buddy is an unofficial community-driven Android app designed to check the balance of your Dhaka MRT Card. It reads the last 10 transactions from the RapidPass / MRT Card. No internet connectivity required. This app is not affiliated with DMTCL, JICA, Government of Bangladesh or any of its affiliates"
        />
        <meta property="og:image" content="/card.jpeg" />

        {/* <!-- Twitter Meta Tags --> */}
        <meta name="twitter:card" content="summary_large_image" />
        <meta property="twitter:domain" content="mrtbuddy.com" />
        <meta property="twitter:url" content="https://mrtbuddy.com/" />
        <meta
          name="twitter:title"
          content="MRT Buddy - Your Dhaka Metro Rail Companion App"
        />
        <meta
          name="twitter:description"
          content="MRT Buddy is an unofficial community-driven Android app designed to check the balance of your Dhaka MRT Card. It reads the last 10 transactions from the RapidPass / MRT Card. No internet connectivity required. This app is not affiliated with DMTCL, JICA, Government of Bangladesh or any of its affiliates"
        />
        <meta property="twitter:image" content="/card.jpeg" />
      </Head>
      <Component {...pageProps} />
      <GoogleAnalytics gaId="G-1YW6R1YDTY" />
    </main>
  );
}
