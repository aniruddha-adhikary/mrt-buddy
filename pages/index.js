import React from 'react';
import { StickyNavbar } from '../components/Navbar';
import Link from 'next/link';
import { motion, AnimatePresence } from 'framer-motion';
import { Download, Apple } from 'lucide-react';
import dynamic from 'next/dynamic';

// Import client-side components
const AnimatedDownloadButtonClient = dynamic(() => import('../components/AnimatedDownloadButton'), { ssr: false });

// GridPattern component
const GridPattern = ({
  className = '',
  width = 40,
  height = 40,
  x = -1,
  y = -1,
  strokeDasharray = 0,
  numSquares = 200,
  maxOpacity = 0.5,
  duration = 1,
  repeatDelay = 0.5
}) => (
  <div className={`absolute inset-0 overflow-hidden ${className}`}>
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      transition={{ duration: 0.5 }}
      className="absolute inset-0"
    >
      <svg
        className="absolute w-full h-full"
        width="100%"
        height="100%"
        viewBox={`${x} ${y} ${width * 10} ${height * 10}`}
        fill="none"
        xmlns="http://www.w3.org/2000/svg"
      >
        {Array.from({ length: numSquares }).map((_, i) => {
          const row = Math.floor(i / 10);
          const col = i % 10;
          return (
            <motion.rect
              key={i}
              x={col * width}
              y={row * height}
              width={width}
              height={height}
              stroke="currentColor"
              strokeWidth="0.5"
              strokeDasharray={strokeDasharray}
              initial={{ opacity: 0 }}
              animate={{ opacity: Math.random() * maxOpacity }}
              transition={{
                duration,
                repeat: Infinity,
                repeatDelay: Math.random() * repeatDelay,
                ease: "linear"
              }}
              className="text-gray-300"
            />
          );
        })}
      </svg>
    </motion.div>
  </div>
);

// iPhone 15 Pro component
const Iphone15Pro = ({ width = 300, height = 600, src, ...props }) => (
  <svg
    width={width}
    height={height}
    viewBox="0 0 433 882"
    fill="none"
    xmlns="http://www.w3.org/2000/svg"
    {...props}
  >
    <path
      d="M2 73C2 32.6832 34.6832 0 75 0H357C397.317 0 430 32.6832 430 73V809C430 849.317 397.317 882 357 882H75C34.6832 882 2 849.317 2 809V73Z"
      className="fill-[#E5E5E5] dark:fill-[#404040]"
    />
    <path
      d="M6 74C6 35.3401 37.3401 4 76 4H356C394.66 4 426 35.3401 426 74V808C426 846.66 394.66 878 356 878H76C37.3401 878 6 846.66 6 808V74Z"
      className="dark:fill-[#262626] fill-white"
    />
    {src && (
      <image
        href={src}
        x="21.25"
        y="19.25"
        width="389.5"
        height="843.5"
        preserveAspectRatio="xMidYMid slice"
        clipPath="url(#roundedCorners)"
      />
    )}
    <defs>
      <clipPath id="roundedCorners">
        <rect
          x="21.25"
          y="19.25"
          width="389.5"
          height="843.5"
          rx="55.75"
          ry="55.75"
        />
      </clipPath>
    </defs>
  </svg>
);

// Confetti component
const Confetti = ({ active }) => (
  active ? (
    <motion.div
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      className="fixed inset-0 pointer-events-none"
    >
      {Array.from({ length: 50 }).map((_, i) => (
        <motion.div
          key={i}
          className="absolute w-2 h-2 bg-indigo-500"
          initial={{
            x: '50vw',
            y: '50vh',
            scale: 0
          }}
          animate={{
            x: `${Math.random() * 100}vw`,
            y: `${Math.random() * 100}vh`,
            scale: [0, 1, 0],
            rotate: [0, 360]
          }}
          transition={{
            duration: 1.5,
            delay: Math.random() * 0.2,
            repeat: Infinity
          }}
        />
      ))}
    </motion.div>
  ) : null
);

// Home component
const HomeComponent = () => {
  const [currentDevice, setCurrentDevice] = React.useState(0);
  const [showConfetti, setShowConfetti] = React.useState(false);
  const [isAnimating, setIsAnimating] = React.useState(false);

  const handleDownloadClick = () => {
    if (!isAnimating) {
      setShowConfetti(true);
      setIsAnimating(true);
      setTimeout(() => {
        setShowConfetti(false);
        setIsAnimating(false);
      }, 2000);
    }
  };

  const devices = [
    '/1.png',
    '/2.jpg'
  ];

  const features = [
    {
      title: 'NFC Card Reader',
      description: 'Instantly check your MRT Pass balance using NFC',
      icon: '📱'
    },
    {
      title: 'Transaction History',
      description: 'View your last 10 transactions',
      icon: '📊'
    },
    {
      title: 'Pass Compatibility',
      description: 'Works with both MRT Pass and Rapid Pass',
      icon: '💳'
    }
  ];

  return (
    <main className="min-h-screen bg-white">
      <StickyNavbar />

      <section className="relative pt-36 sm:pt-40 pb-16 sm:pb-20 overflow-hidden">
        <GridPattern
          className="opacity-30"
          numSquares={150}
          maxOpacity={0.3}
        />
        <div className="relative z-10 max-w-7xl mx-auto px-4 sm:px-6 text-center">
          <div className="max-w-3xl mx-auto mb-12 sm:mb-20">
            <motion.div
              initial={{ letterSpacing: '0em' }}
              animate={{ letterSpacing: '0.1em' }}
              transition={{ duration: 1, ease: 'easeOut' }}
            >
              <h1 className="text-4xl sm:text-5xl lg:text-6xl font-bold tracking-tight bg-clip-text text-transparent bg-gradient-to-r from-gray-900 to-gray-600 mb-4 sm:mb-6">
                Your Smart MRT Companion
              </h1>
            </motion.div>
            <p className="text-lg sm:text-xl text-gray-600 leading-relaxed">
              Manage your MRT cards, check fares, and track your journeys with ease using NFC technology
            </p>
            <div className="flex flex-col sm:flex-row justify-center gap-4 mt-8">
              <AnimatedDownloadButtonClient
                platform="android"
                icon={Download}
                initialText="Download for Android"
                changeText="Downloaded!"
                onClick={handleDownloadClick}
                isAnimating={isAnimating}
              />
              <AnimatedDownloadButtonClient
                platform="ios"
                icon={Apple}
                initialText="Download for iOS"
                changeText="Downloaded!"
                onClick={handleDownloadClick}
                isAnimating={isAnimating}
              />
            </div>
          </div>
        </div>
      </section>

      <section className="py-4 sm:py-8 md:py-12 overflow-hidden">
        <div className="max-w-[1400px] mx-auto px-4 sm:px-6">
          <div className="flex flex-col md:flex-row justify-center items-center gap-6 md:gap-8">
            {devices.map((device, index) => (
              <div key={index} className="w-full px-4 sm:px-0">
                <div className="relative max-w-[280px] sm:max-w-[400px] md:max-w-[500px] lg:max-w-[652px] mx-auto">
                  <Iphone15Pro
                    width="100%"
                    height="652"
                    src={device}
                    className="w-full transform-gpu hover:scale-102 transition-transform duration-300 ease-in-out"
                    style={{
                      maxWidth: '100%',
                      height: 'auto',
                      aspectRatio: '408/652'
                    }}
                  />
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      <section className="py-16 sm:py-24 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6">
          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
            {features.map((feature, index) => (
              <motion.div
                key={feature.title}
                className="feature-card"
                whileHover={{ scale: 1.02 }}
                initial={{ opacity: 0, y: 20 }}
                animate={{ opacity: 1, y: 0 }}
                transition={{ delay: index * 0.1 }}
              >
                <div className="relative bg-white p-6 rounded-lg">
                  <span className="text-4xl mb-4 block">{feature.icon}</span>
                  <h3 className="text-xl font-semibold mb-2">{feature.title}</h3>
                  <p className="text-gray-600">{feature.description}</p>
                </div>
              </motion.div>
            ))}
          </div>
        </div>
      </section>

      <section id="download" className="py-16 sm:py-24">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 text-center">
          <div className="flex flex-col sm:flex-row justify-center gap-4">
            <a href="https://github.com/aniruddha-adhikary/mrt-buddy/releases/latest/download/app-release.apk">
              <AnimatedDownloadButtonClient
                platform="android"
                icon={Download}
                initialText="Download for Android"
                changeText="Downloaded!"
                onClick={handleDownloadClick}
                isAnimating={isAnimating}
              />
            </a>
          </div>
          <p className="text-gray-600 mt-4">⚠️ While our app is pending Play Store approval, get updates on our WhatsApp channel:</p>
          <a href="https://whatsapp.com/channel/0029VaurMehLI8Yeb3STq42g" className="text-blue-600 hover:text-blue-800 mt-2 inline-block">
            Join WhatsApp Channel
          </a>
          <p className="text-sm text-gray-500 mt-8">
            This is an independent project and is not officially endorsed by or affiliated with Dhaka Mass Transit Company Limited (DMTCL).
          </p>
          <AnimatePresence>
            {showConfetti && <Confetti active={showConfetti} />}
          </AnimatePresence>
        </div>
      </section>

      <footer className="py-8 text-center text-gray-600 border-t border-gray-100">
        <p className="text-sm">
          Built with ❤️ by{' '}
          <a
            href="https://irfanhasan.vercel.app"
            target="_blank"
            rel="noopener noreferrer"
            className="text-blue-600 hover:text-blue-800 transition-colors"
          >
            Irfan
          </a>
        </p>
      </footer>
    </main>
  );
};

// Export the client-side wrapped component
export default dynamic(() => Promise.resolve(HomeComponent), { ssr: false });
