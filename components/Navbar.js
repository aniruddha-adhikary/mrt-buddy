import { MoonIcon, SunIcon, ChevronDownIcon } from "@heroicons/react/20/solid";
import Link from "next/link";
import { useRouter } from "next/router";
import React, { useEffect, useState } from "react";
import { useTheme } from "../contexts";

export const StickyNavbar = () => {
  const [isNavOpen, setIsNavOpen] = useState(false);
  const [isHelpOpen, setIsHelpOpen] = useState(false);
  const { isDarkMode, toggleDarkMode } = useTheme();
  const router = useRouter();

  const toggleNav = () => {
    setIsNavOpen((prevState) => !prevState);
  };

  // Close menu when route changes
  useEffect(() => {
    const handleRouteChange = () => {
      setIsNavOpen(false);
    };

    router.events.on("routeChangeComplete", handleRouteChange);
    router.events.on("routeChangeStart", handleRouteChange);

    return () => {
      router.events.off("routeChangeComplete", handleRouteChange);
      router.events.off("routeChangeStart", handleRouteChange);
    };
  }, [router]);

  // Prevent body scroll when menu is open
  useEffect(() => {
    if (isNavOpen) {
      document.body.style.overflow = "hidden";
    } else {
      document.body.style.overflow = "unset";
    }
    return () => {
      document.body.style.overflow = "unset";
    };
  }, [isNavOpen]);

  const mainNavLinks = [
    { href: "/", label: "Home" },
    { href: "/#download", label: "Download" },
    { href: "/contributors", label: "Contributors" },
    {
      href: "http://github.com/aniruddha-adhikary/mrt-buddy",
      label: "GitHub",
      external: true,
    },
  ];

  const helpNavLinks = [
    { href: "/tutorials", label: "Tutorials" },
    { href: "/faq", label: "FAQ" },
    { href: "/support", label: "Support" },
    { href: "/devices", label: "Supported Devices" },
    { href: "/privacy-policy", label: "Privacy Policy" },
  ];

  return (
    <div className="relative w-full">
      <header className="fixed top-0 left-0 right-0 z-50 bg-white dark:bg-[#121212] border-b border-gray-100 dark:border-gray-800">
        <div className="max-w-7xl mx-auto px-4">
          <div className="flex items-center justify-between h-16">
            <Link
              href="/"
              className="text-xl font-medium flex items-center dark:text-white"
            >
              <img src="/icon-512.png" className="h-10 w-10 mr-2" />
              <span>MRT Buddy</span>
            </Link>

            {/* Desktop Menu */}
            <div className="hidden lg:flex items-center space-x-8">
              {mainNavLinks.map((link) => (
                <Link
                  key={link.href}
                  href={link.href}
                  className="text-gray-900 hover:text-gray-600 dark:text-white dark:hover:text-white/[90%]"
                  target={link.external ? "_blank" : undefined}
                >
                  {link.label}
                </Link>
              ))}

              {/* Help Dropdown */}
              <div className="relative">
                <button
                  onClick={() => setIsHelpOpen(!isHelpOpen)}
                  className="flex items-center text-gray-900 hover:text-gray-600 dark:text-white dark:hover:text-white/[90%]"
                >
                  Help
                  <ChevronDownIcon className="w-4 h-4 ml-1" />
                </button>

                {isHelpOpen && (
                  <div className="absolute right-0 mt-2 py-2 w-48 bg-white dark:bg-gray-800 rounded-md shadow-xl z-50">
                    {helpNavLinks.map((link) => (
                      <Link
                        key={link.href}
                        href={link.href}
                        className="block px-4 py-2 text-sm text-gray-700 hover:bg-gray-100 dark:text-white dark:hover:bg-gray-700"
                        onClick={() => setIsHelpOpen(false)}
                      >
                        {link.label}
                      </Link>
                    ))}
                  </div>
                )}
              </div>
              <button onClick={toggleDarkMode}>
                {isDarkMode ? (
                  <SunIcon className="w-6 h-6 dark:text-white dark:hover:text-white/[90%]" />
                ) : (
                  <MoonIcon className="h-6 w-6 text-slate-500 hover:text-slate-600" />
                )}
              </button>
            </div>

            {/* Mobile Menu Button */}
            <div className="lg:hidden">
              <button className="mr-1" onClick={toggleDarkMode}>
                {isDarkMode ? (
                  <SunIcon className="w-6 h-6 dark:text-white" />
                ) : (
                  <MoonIcon className="h-6 w-6 text-slate-500" />
                )}
              </button>
              <button
                onClick={toggleNav}
                className="inline-flex items-center justify-center p-2 rounded-md text-gray-900 dark:text-white dark:hover:bg-gray-700 hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-inset focus:ring-gray-500"
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
                    d={
                      isNavOpen
                        ? "M6 18L18 6M6 6l12 12"
                        : "M4 6h16M4 12h16M4 18h16"
                    }
                  />
                </svg>
              </button>
            </div>
          </div>

          {/* Mobile Menu */}
          <div
            className={`fixed inset-0 z-40 bg-white dark:bg-[#121212] transform transition-all duration-300 ease-in-out lg:hidden ${
              isNavOpen
                ? "translate-x-0 opacity-100"
                : "translate-x-full opacity-0"
            }`}
            style={{ top: "64px", height: "calc(100vh - 64px)" }}
          >
            <nav className="px-2 pt-2 pb-3 space-y-1">
              {mainNavLinks.map((link) => (
                <Link
                  key={link.href}
                  href={link.href}
                  className="block px-3 py-2 rounded-md text-base font-medium text-gray-900 hover:bg-gray-100 dark:text-white dark:hover:bg-gray-900"
                  onClick={toggleNav}
                  target={link.external ? "_blank" : undefined}
                >
                  {link.label}
                </Link>
              ))}

              <div className="px-3 py-2 font-medium text-gray-900 dark:text-white">
                Help
              </div>
              {helpNavLinks.map((link) => (
                <Link
                  key={link.href}
                  href={link.href}
                  className="block px-6 py-2 rounded-md text-base font-medium text-gray-900 hover:bg-gray-100 dark:text-white dark:hover:bg-gray-900"
                  onClick={toggleNav}
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
