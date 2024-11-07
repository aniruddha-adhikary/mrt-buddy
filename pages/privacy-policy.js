import React from 'react';
import { StickyNavbar } from '../components/Navbar';
import Link from 'next/link';

export default function PrivacyPolicy() {
  return (
    <div className="min-h-screen bg-white">
      <StickyNavbar />

      <main className="container mx-auto px-4 pt-24 pb-16">
        <div className="max-w-3xl mx-auto">
          <h1 className="text-4xl font-bold mb-8">Privacy Policy</h1>
          
          <p className="text-sm text-gray-600 mb-4">Last updated: 4th November, 2024</p>
          
          <section className="space-y-8">
            <div>
              <h2 className="text-2xl font-semibold mb-4">1. Introduction</h2>
              <p className="text-gray-700">
                Welcome to <strong>MRT Buddy</strong> ("we", "us", or "our"). This Privacy Policy outlines how we handle any personal information we may access when you use our Android application. Your privacy is of utmost importance to us, and we are committed to protecting it.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">2. Data Collection and Use</h2>
              <p className="text-gray-700">
                <strong>MRT Buddy</strong> uses Near Field Communication (NFC) technology to read data from your transit cards. The app accesses the balance and trip information stored directly on the card itself. We do <strong>not</strong> collect, store, or transmit any personal data from your transit card or your device.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">3. No Internet Connectivity</h2>
              <p className="text-gray-700">
                Our app operates entirely offline. It does not connect to the internet or any external servers. As a result, no data is transmitted off your device at any time.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">4. Data Sharing</h2>
              <p className="text-gray-700">
                Since we do not collect or store any of your personal information, we do not share any data with third parties.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">5. Data Security</h2>
              <p className="text-gray-700">
                While our app does not store any personal data, we are committed to ensuring that your information remains secure during use. The app only reads the necessary data to display it on your device and does not retain any information afterward.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">6. Children's Privacy</h2>
              <p className="text-gray-700">
                Our app is not intended for use by children under the age of 13. We do not knowingly collect personal information from children. If you believe that a child has provided us with personal data, please contact us so we can take appropriate action.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">7. Changes to This Privacy Policy</h2>
              <p className="text-gray-700">
                We may update our Privacy Policy from time to time. Any changes will be posted on this page with an updated "Last updated" date. We encourage you to review this Privacy Policy periodically.
              </p>
            </div>

            <div>
              <p className="text-gray-700 mt-8">
                By using <strong>MRT Buddy</strong>, you agree to the terms outlined in this Privacy Policy.
              </p>
            </div>
          </section>
        </div>
      </main>

      <footer className="container mx-auto px-4 py-8 text-center text-gray-600">
        <p>
          Website developed by{" "}
          <a
            href="https://irfanhasan.vercel.app"
            className="text-blue-600 hover:underline"
            target="_blank"
            rel="noopener noreferrer"
          >
            Irfan
          </a>
        </p>
      </footer>
    </div>
  );
}
