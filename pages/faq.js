import React from "react";
import Head from "next/head";
import { StickyNavbar } from "../components/Navbar";
import { Footer } from "../components/Footer";
import { CommunitySection } from "../components/CommunitySection";

export default function FAQ() {
  return (
    <div className="min-h-screen bg-white dark:bg-[#121212]">
      <Head>
        <title>FAQ - MRT Buddy</title>
      </Head>
      <StickyNavbar />
      <CommunitySection />

      <main className="container mx-auto px-4 pt-24 dark:text-white">
        <div className="max-w-3xl mx-auto">
          <h1 className="text-4xl font-bold mb-8">
            Frequently Asked Questions
          </h1>

          <p className="text-sm text-gray-600 mb-8 dark:text-gray-50">
            Last updated: 24th November, 2024
          </p>

          <section className="space-y-8">
            <div>
              <h2 className="text-2xl font-semibold mb-4">
                Core Functionality
              </h2>

              <div className="space-y-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2 dark:text-gray-50">
                    What does MRT Buddy do?
                  </h3>
                  <p className="text-gray-700 dark:text-gray-50">
                    MRT Buddy is designed to help you manage your transit
                    experience by scanning your Dhaka MRT Pass or RapidPass. The
                    app is fully compatible with both card types and displays your 
                    current card balance and shows you your last 10 transactions, 
                    making it easy to track your transit usage.
                  </p>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 dark:text-gray-50 mb-2">
                    How does the app handle my data? Is it secure?
                  </h3>
                  <p className="text-gray-700 dark:text-gray-50">
                    Your privacy is our top priority. MRT Buddy operates
                    completely offline and only extracts data directly from your
                    card to display it to you. The last 20 trip details are stored 
                    directly on your card itself - your card is like a small computer 
                    that is powered by the NFC reader. The app simply reads this data 
                    when you scan your card. No servers are involved, and no data is 
                    stored elsewhere. You own your data, which stays securely in your 
                    wallet on your card.
                  </p>
                  <p className="text-gray-700 dark:text-gray-50 mt-2">
                    The app can only read your card data when you physically have the card in your possession - 
                    just like checking your balance at an MRT station kiosk.
                  </p>
                </div>
              </div>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">Availability</h2>

              <div className="space-y-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2 dark:text-gray-50">
                    Is the app available on the official app stores?
                  </h3>
                  <p className="text-gray-700 dark:text-gray-50">
                    Yes! We finally got the approval from Google. 
                    You can now download MRT Buddy from the <a href="https://play.google.com/store/apps/details?id=net.adhikary.mrtbuddy" className="text-blue-500 underline">PlayStore</a> and the <a href="https://apps.apple.com/us/app/mrt-buddy/id6737849667" className="text-blue-500 underline">AppStore</a>.
                  </p>
                </div>
              </div>
            </div>

            <div>
              <h2 className="text-2xl font-semibold mb-4">Technical Details</h2>

              <div className="space-y-6">
                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2 dark:text-gray-50">
                    Does the app have API access to the Dhaka MRT System?
                  </h3>
                  <p className="text-gray-700 dark:text-gray-50">
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
                  <p className="text-gray-700 dark:text-gray-50">
                    The app is completely secure and cannot modify your card
                    balance. The MRT cards use advanced FeliCa chip technology with 
                    military-grade security features. The system implements
                    strict security measures that make tampering mathematically impossible:
                  </p>
                  <ul className="list-disc pl-6 mt-2 space-y-2 text-gray-700 dark:text-gray-50">
                    <li>
                      The card uses cryptographic signatures that require secret keys 
                      held exclusively by DMTCL (Dhaka Mass Transit Company Limited)
                    </li>
                    <li>
                      Even if someone could write data to the card, the cryptographic 
                      signatures would not match, making the card invalid
                    </li>
                    <li>
                      Personal information like cardholder details are stored only on 
                      DMTCL's secure servers, not on the card itself
                    </li>
                    <li>
                      The app can only read basic transaction data - it cannot access 
                      any personal information or modify any card data
                    </li>
                  </ul>
                  <p className="text-gray-700 dark:text-gray-50 mt-2">
                    While the app stores your transaction history locally on your device
                    to enhance your experience, we recommend securing your device to
                    prevent unauthorized access to this data.
                  </p>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2 dark:text-gray-50">
                    Can I use my phone's NFC to imitate the MRT card?
                  </h3>
                  <p className="text-gray-700 dark:text-gray-50">
                    Unfortunately no. This feature would need to be implemented by the Metro Rail Authority themselves, 
                    or by a company licensed by them. Third-party apps cannot implement this functionality due to the 
                    security measures in place.
                  </p>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2 dark:text-gray-50">
                    Can I use my phone for check-in and check-out?
                  </h3>
                  <p className="text-gray-700 dark:text-gray-50">
                    Unfortunately, check-in and check-out functionality using phones can only be implemented with direct 
                    cooperation from the Metro Rail authorities. This requires special integration with their systems 
                    and security protocols that are not available to third-party applications.
                  </p>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2 dark:text-gray-50">
                    Can I top up my card balance through the app?
                  </h3>
                  <p className="text-gray-700 dark:text-gray-50">
                    No, card top-ups can only be done at official MRT stations or authorized points. This is because:
                  </p>
                  <ul className="list-disc pl-6 mt-2 space-y-2 text-gray-700 dark:text-gray-50">
                    <li>
                      Writing to the card requires special encryption keys held only by DMTCL
                    </li>
                    <li>
                      The card's secure blocks are encrypted and do not allow unauthorized modifications
                    </li>
                    <li>
                      E-wallet or digital payment integration would require official authorization from Metro authorities
                    </li>
                  </ul>
                </div>

                <div>
                  <h3 className="text-lg font-semibold text-gray-900 mb-2 dark:text-gray-50">
                    Does the app work on phones without NFC?
                  </h3>
                  <p className="text-gray-700 dark:text-gray-50">
                    No, NFC (Near Field Communication) is required to communicate with your Metro card. Without NFC hardware 
                    in your phone, the app cannot read the card data. This is a hardware limitation that cannot be 
                    worked around with software.
                  </p>
                </div>
              </div>
            </div>

            <div className="pt-8">
              <p className="text-gray-700 dark:text-gray-50">
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
