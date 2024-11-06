import React from 'react';
import { StickyNavbar } from '../components/Navbar';
import Link from 'next/link';
import contributors from './contributors.json';

export default function Contributors() {
  return (
    <main className="flex min-h-screen flex-col items-center">
      <StickyNavbar />

      <section className="relative pt-28 sm:pt-32 pb-16 sm:pb-20 overflow-hidden w-full">
        <div className="relative z-10">
          <h1 className="text-4xl sm:text-5xl font-bold text-center mb-8">
            Contributors
          </h1>

          <div className="max-w-7xl mx-auto px-4 sm:px-6">
            <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 gap-6 justify-items-center">
              {contributors.map((contributor) => (
                <a
                  key={contributor.username}
                  href={`https://github.com/${contributor.username}`}
                  target="_blank"
                  rel="noopener noreferrer"
                  className="group flex flex-col items-center bg-white p-4 rounded-lg shadow-sm hover:shadow-md transition-shadow"
                >
                  <img
                    src={contributor.avatar_url}
                    alt={contributor.username}
                    className="w-24 h-24 rounded-full mb-3 transition-transform group-hover:scale-105"
                  />
                  <h3 className="text-sm font-medium text-gray-900">
                    {contributor.username}
                  </h3>
                </a>
              ))}
            </div>
          </div>
        </div>
      </section>

      {/* Developer Credit */}
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
}
