import dynamic from "next/dynamic";
import Head from "next/head";
import React, { useState } from "react";
import { DeviceShowcase } from "../components/DeviceShowcase";
import { DownloadSection } from "../components/DownloadSection";
import { Features } from "../components/Features";
import { Footer } from "../components/Footer";
import { Hero } from "../components/Hero";
import { StickyNavbar } from "../components/Navbar";

const HomeComponent = () => {
  const [isAnimating, setIsAnimating] = React.useState(false);
  const [isDarkMode, setIsDarkMode] = useState(false);

  const handleDownloadClick = () => {
    if (!isAnimating) {
      setIsAnimating(true);
      setTimeout(() => {
        setIsAnimating(false);
      }, 2000);
    }
  };

  const toggleDarkMode = () => {
    setIsDarkMode((prevState) => !prevState);
  };

  return (
    <main className={`min-h-screen bg-white ${isDarkMode && "dark"}`}>
      <Head>
        <title>MRT Buddy - Your Dhaka Metro Rail Companion</title>
      </Head>
      <StickyNavbar toggleDarkMode={toggleDarkMode} darkMode={isDarkMode} />
      <Hero
        handleDownloadClick={handleDownloadClick}
        isAnimating={isAnimating}
      />
      <DeviceShowcase />
      <Features />
      <DownloadSection
        handleDownloadClick={handleDownloadClick}
        isAnimating={isAnimating}
      />
      <Footer />
    </main>
  );
};

// Export the client-side wrapped component
export default dynamic(() => Promise.resolve(HomeComponent), { ssr: false });
