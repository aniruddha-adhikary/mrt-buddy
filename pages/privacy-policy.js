import React from "react";
import Head from "next/head";
import { StickyNavbar } from "../components/Navbar";
import { Footer } from "../components/Footer";
import { CommunitySection } from "../components/CommunitySection";

export default function PrivacyPolicy() {
  return (
    <div className="min-h-screen bg-white dark:bg-[#121212]">
      <Head>
        <title>Privacy Policy - MRT Buddy</title>
      </Head>
      <StickyNavbar />
      <CommunitySection />

      <main className="container mx-auto px-4 pt-24 pb-16">
        <div className="max-w-3xl mx-auto">
          <h1 className="text-4xl font-bold mb-8 dark:text-white">
            Privacy Policy
          </h1>

          <p className="text-sm text-gray-600 mb-4 dark:text-gray-50">
            Last updated: 13th November, 2024
          </p>

          <section className="space-y-8 dark:text-white">
            <div>
              <h2 className="text-2xl font-semibold mb-4">1. Introduction</h2>
              <p className="text-gray-700 dark:text-gray-50">
                Welcome to <strong>MRT Buddy</strong> ("we", "us", or "our").
                This Privacy Policy outlines how we handle any personal
                information we may access when you use our Android application.
                Your privacy is of utmost importance to us, and we are committed
                to protecting it.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">
                2. Data Collection and Use
              </h2>
              <p className="text-gray-700 dark:text-gray-50">
                <strong>MRT Buddy</strong> uses Near Field Communication (NFC)
                technology to read data from your transit cards. The app
                accesses the balance and trip information stored directly on the
                card itself. To enhance your experience and provide a more
                complete usage history, the app now stores transaction data from
                your transit cards on your device. This allows you to accumulate
                more records over time as you scan your card.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">3. Data Storage</h2>
              <p className="text-gray-700 dark:text-gray-50">
                All transaction data collected from your transit cards is stored
                locally on your device. We do not collect, store, or transmit
                any of your personal data to our servers or any third parties.
                The data never leaves your device.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">
                4. No Internet Connectivity
              </h2>
              <p className="text-gray-700 dark:text-gray-50">
                Our app operates entirely offline. It does not connect to the
                internet or any external servers. As a result, no data is
                transmitted off your device at any time.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">4. Data Sharing</h2>
              <p className="text-gray-700 dark:text-gray-50">
                Since we do not collect or store any of your personal
                information on our servers, we do not share any data with third
                parties. All data remains on your device.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">5. Data Security</h2>
              <p className="text-gray-700 dark:text-gray-50">
                While our app stores transaction data on your device to provide
                you with an extended usage history, we are committed to ensuring
                that your information remains secure. The app only accesses and
                retains the necessary data to enhance your experience. We
                recommend securing your device to prevent unauthorized access.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">
                6. Children's Privacy
              </h2>
              <p className="text-gray-700 dark:text-gray-50">
                Our app is not intended for use by children under the age of 13.
                We do not knowingly collect personal information from children.
                If you believe that a child has provided us with personal data,
                please contact us so we can take appropriate action.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">
                7. Changes to This Privacy Policy
              </h2>
              <p className="text-gray-700 dark:text-gray-50">
                We may update our Privacy Policy from time to time. Any changes
                will be posted on this page with an updated "Last updated" date.
                We encourage you to review this Privacy Policy periodically.
              </p>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">8. Changelog</h2>
              <div className="text-gray-700 dark:text-gray-50">
                <ul className="list-disc pl-5 space-y-2">
                  <li>
                    <strong>November 13, 2024:</strong>
                    <ul className="list-circle pl-5 mt-1">
                      <li>Added local storage for transaction history</li>
                      <li>
                        Updated Data Collection and Use section to reflect new
                        storage functionality
                      </li>
                      <li>Added new Data Storage section</li>
                      <li>
                        Updated Data Security section with device security
                        recommendations
                      </li>
                    </ul>
                  </li>
                </ul>
              </div>
            </div>

            <div>
              <p className="text-gray-700 mt-8 dark:text-gray-50">
                By using <strong>MRT Buddy</strong>, you agree to the terms
                outlined in this Privacy Policy.
              </p>
            </div>
          </section>
        </div>
      </main>

      <Footer />
    </div>
  );
}
