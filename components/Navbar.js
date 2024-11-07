import React, { useState, useEffect } from "react";
import Link from "next/link";
import { useRouter } from 'next/router';

export const StickyNavbar = () => {
  const [isNavOpen, setIsNavOpen] = useState(false);
  const router = useRouter();

  const toggleNav = () => {
    setIsNavOpen(prevState => !prevState);
  };

  // Close menu when route changes
  useEffect(() => {
    const handleRouteChange = () => {
      setIsNavOpen(false);
    };

    router.events.on('routeChangeComplete', handleRouteChange);
    router.events.on('routeChangeStart', handleRouteChange);

    return () => {
      router.events.off('routeChangeComplete', handleRouteChange);
      router.events.off('routeChangeStart', handleRouteChange);
    };
  }, [router]);

  // Prevent body scroll when menu is open
  useEffect(() => {
    if (isNavOpen) {
      document.body.style.overflow = 'hidden';
    } else {
      document.body.style.overflow = 'unset';
    }
    return () => {
      document.body.style.overflow = 'unset';
    };
  }, [isNavOpen]);

  const navLinks = [
    { href: '/', label: 'Home' },
    { href: '/contributors', label: 'Contributors' },
    { href: '/privacy-policy', label: 'Privacy Policy' },
    { href: '/#download', label: 'Download' },
    { href: 'http://github.com/aniruddha-adhikary/mrt-buddy', label: 'GitHub', external: true },
  ];

  return (
    <div className="relative w-full">
      <header className="fixed top-0 left-0 right-0 z-50 bg-white border-b border-gray-100">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex items-center justify-between h-16">
            <Link href="/" className="text-xl font-medium flex items-center">
              <img src="/icon-512.png" className="h-10 w-10 mr-2" />
              <span>MRT Buddy</span>
            </Link>

            {/* Desktop Menu */}
            <div className="hidden lg:flex items-center space-x-8">
              {navLinks.map((link) => (
                <Link
                  key={link.href}
                  href={link.href}
                  className="text-gray-900 hover:text-gray-600"
                  target={link.external ? '_blank' : undefined}
                >
                  {link.label}
                </Link>
              ))}
            </div>

            {/* Mobile Menu Button */}
            <button
              onClick={toggleNav}
              className="inline-flex items-center justify-center p-2 rounded-md text-gray-900 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-gray-500 lg:hidden"
              aria-expanded={isNavOpen}
            >
              <span className="sr-only">Open main menu</span>
              <svg
                className="h-6 w-6"
                xmlns="http://www.w3.org/2000/svg"
                fill="none"
                viewBox="0 0 24 24"
                stroke="currentColor"
                aria-hidden="true"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth="2"
                  d={isNavOpen ? 'M6 18L18 6M6 6l12 12' : 'M4 6h16M4 12h16M4 18h16'}
                />
              </svg>
            </button>
          </div>

          {/* Mobile Menu */}
          <div
            className={`fixed inset-0 z-40 bg-white transform transition-all duration-300 ease-in-out lg:hidden ${isNavOpen ? 'translate-x-0 opacity-100' : 'translate-x-full opacity-0'
              }`}
            style={{ top: '64px', height: 'calc(100vh - 64px)' }}
          >
            <nav className="px-2 pt-2 pb-3 space-y-1">
              {navLinks.map((link) => (
                <Link
                  key={link.href}
                  href={link.href}
                  className="block px-3 py-2 rounded-md text-base font-medium text-gray-900 hover:bg-gray-100"
                  onClick={toggleNav}
                  target={link.external ? '_blank' : undefined}
                >
                  {link.label}
                </Link>
              ))}
            </nav>
          </div>
        </div>
      </header>
      <div className="h-16" /> {/* Spacer for fixed header */}
    </div>
  );
};

export default StickyNavbar;