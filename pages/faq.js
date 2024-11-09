import Head from "next/head";
import React from "react";
import { Footer } from "../components/Footer";
import { StickyNavbar } from "../components/Navbar";

export default function FAQ() {
  return (
    <div className="min-h-screen bg-white">
      <Head>
        <title>FAQ - MRT Buddy</title>
      </Head>
      <StickyNavbar />

      <main className="container mx-auto px-4 pt-24">
        <div className="max-w-3xl mx-auto">
          <h1 className="text-4xl font-bold mb-8">
            Frequently Asked Questions
          </h1>

          <p className="text-sm text-gray-600 mb-8">
            Last updated: 4th November, 2024
          </p>

          <section className="space-y-8">
            <div>
              <h2 className="text-2xl font-semibold mb-4">
                Core Functionality
              </h2>

              <div className="space-y-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    What does MRT Buddy do?
                  </h3>
                  <p className="text-gray-700">
                    MRT Buddy is designed to help you manage your transit
                    experience by scanning your Dhaka MRT Pass or RapidPass. The
                    app displays your current card balance and shows you your
                    last 10 transactions, making it easy to track your transit
                    usage.
                  </p>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    How does the app handle my data?
                  </h3>
                  <p className="text-gray-700">
                    Your privacy is our top priority. MRT Buddy operates
                    completely offline and only extracts data directly from your
                    card to display it to you. The app has no internet
                    connectivity permissions, which means your data never leaves
                    your device. All information is processed locally and
                    temporarily for display purposes only.
                  </p>
                </div>
              </div>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">Availability</h2>

              <div className="space-y-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    Why isn't the app available on the Google Play Store?
                  </h3>
                  <p className="text-gray-700">
                    The app is currently undergoing Google's review process.
                    We've submitted it to the Play Store and are working to
                    ensure it meets all of Google's quality and security
                    standards. We'll make it available as soon as the review
                    process is complete.
                  </p>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    Is there an iOS version available?
                  </h3>
                  <p className="text-gray-700">
                    Yes, we've developed an iOS version of MRT Buddy! It's
                    currently in Apple's review process. Once approved, it will
                    be available on the App Store.
                  </p>
                </div>
              </div>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">Technical Details</h2>

              <div className="space-y-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    Does the app have API access to the Dhaka MRT System?
                  </h3>
                  <p className="text-gray-700">
                    No, we don't have or need direct API access to the Dhaka MRT
                    System. The Bangladeshi MRT Card uses sophisticated Japanese
                    transit system technology, specifically Sony FeliCa cards.
                    These cards are designed to store your last 10 transactions
                    directly on the card itself. We calculate your balance by
                    reading this stored transaction data from your card.
                  </p>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2">
                    Is the app secure? Can it modify my card balance?
                  </h3>
                  <p className="text-gray-700">
                    The app is completely secure and cannot modify your card
                    balance. The MRT cards use advanced chip technology that
                    functions like a small computer. The system implements
                    strict security measures:
                  </p>
                  <ul className="list-disc pl-6 mt-2 space-y-2 text-gray-700">
                    <li>
                      Access to card data is strictly controlled and limited to
                      specific regions
                    </li>
                    <li>
                      Writing to the card requires an encryption key that only
                      DMTCL (Dhaka Mass Transit Company Limited) possesses
                    </li>
                    <li>
                      The app can only read specific, permitted information from
                      your card
                    </li>
                    <li>
                      The entire MRT system's security architecture ensures that
                      card balances cannot be tampered with
                    </li>
                  </ul>
                </div>
              </div>
            </div>

            <div className="pt-8">
              <p className="text-gray-700">
                Have more questions? Feel free to reach out to us through our
                support channels.
              </p>
            </div>
          </section>
        </div>
      </main>

      <Footer />
    </div>
  );
}
