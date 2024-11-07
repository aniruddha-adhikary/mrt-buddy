import React from "react";
import Link from "next/link";
import Script from "next/script";

export function StickyNavbar() {
  return (
    <div className="max-h-[768px] w-full">
      <header className="sticky top-0 z-10 h-max max-w-full rounded-none px-4 py-2 lg:px-8 lg:py-4 bg-white border-b border-gray-100">
        <nav className="flex items-center justify-between text-blue-gray-900">
          <Link href="/" className="mr-4 cursor-pointer py-1.5 font-medium">
            MRT Buddy
          </Link>
          <div className="flex items-center gap-4">
            <div className="mr-4 hidden lg:block">
              <ul className="mt-2 mb-4 flex flex-col gap-2 lg:mb-0 lg:mt-0 lg:flex-row lg:items-center lg:gap-6">
                <li className="p-1 font-normal">
                  <Link href="/" className="flex items-center text-gray-900 hover:text-gray-600">
                    Home
                  </Link>
                </li>
                <li className="p-1 font-normal">
                  <Link href="/contributors" className="flex items-center text-gray-900 hover:text-gray-600">
                    Contributors
                  </Link>
                </li>
                <li className="p-1 font-normal">
                  <Link href="/privacy-policy" className="flex items-center text-gray-900 hover:text-gray-600">
                    Privacy Policy
                  </Link>
                </li>
              </ul>
            </div>
            <Link
              href="/#download"
              className="hidden lg:inline-block text-gray-900 hover:text-gray-600"
            >
              Download
            </Link>
            <button
              id="mobile-nav-button"
              className="ml-auto h-6 w-6 text-inherit hover:bg-transparent focus:bg-transparent active:bg-transparent lg:hidden"
            >
              <svg
                xmlns="http://www.w3.org/2000/svg"
                className="h-6 w-6"
                fill="none"
                stroke="currentColor"
                strokeWidth={2}
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  d="M4 6h16M4 12h16M4 18h16"
                />
              </svg>
            </button>
          </div>
        </nav>
        <div id="mobile-nav" className="hidden">
          <nav className="block lg:hidden">
            <ul className="mt-2 mb-4 flex flex-col gap-2">
              <li className="p-1 font-normal">
                <Link href="/" className="flex items-center text-gray-900 hover:text-gray-600">
                  Home
                </Link>
              </li>
              <li className="p-1 font-normal">
                <Link href="/contributors" className="flex items-center text-gray-900 hover:text-gray-600">
                  Contributors
                </Link>
              </li>
              <li className="p-1 font-normal">
                <Link href="/privacy-policy" className="flex items-center text-gray-900 hover:text-gray-600">
                  Privacy Policy
                </Link>
              </li>
              <li className="p-1 font-normal">
                <Link href="/#download" className="flex items-center text-gray-900 hover:text-gray-600">
                  Download
                </Link>
              </li>
            </ul>
          </nav>
        </div>
      </header>
      <Script id="nav-script">
        {`
          document.getElementById('mobile-nav-button').addEventListener('click', function() {
            const mobileNav = document.getElementById('mobile-nav');
            const isHidden = mobileNav.classList.contains('hidden');
            if (isHidden) {
              mobileNav.classList.remove('hidden');
            } else {
              mobileNav.classList.add('hidden');
            }
          });
        `}
      </Script>
    </div>
  );
}
