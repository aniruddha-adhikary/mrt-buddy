import { sendGAEvent } from "@next/third-parties/google";
import React from "react";
import AndroidDownloadButton from "./AndroidDownloadButton";

export const DownloadSection = ({ handleDownloadClick, isAnimating }) => (
  <section id="download" className="py-16 sm:py-24 dark:bg-[#121212]">
    <div className="max-w-7xl mx-auto px-4 sm:px-6 text-center">
      <div className="flex flex-row items-center justify-center gap-4">
        <a href="https://github.com/aniruddha-adhikary/mrt-buddy/releases/latest/download/app-release.apk">
          <AndroidDownloadButton
            onClick={() => {
              handleDownloadClick();
              sendGAEvent({ event: "download", value: "android" });
            }}
            isClicked={isAnimating}
          />
        </a>
        <a href="https://apps.apple.com/app/mrt-buddy/id6737849667">
          <img
            src="/app_store.svg"
            alt="Download on the App Store"
            style={{ width: "150px", height: "auto" }}
          />
        </a>
      </div>
      <p className="text-gray-600 mt-4 dark:text-gray-50">
        ⚠️ While our app is pending Play Store approval, get updates on our
        WhatsApp channel:
      </p>
      <a
        href="https://whatsapp.com/channel/0029VaurMehLI8Yeb3STq42g"
        className="text-blue-600 hover:text-blue-800 mt-2 inline-block"
      >
        Join WhatsApp Channel
      </a>
      <p className="text-sm text-gray-500 mt-8 dark:text-gray-50">
        This is an independent project and is not officially endorsed by or
        affiliated with Dhaka Mass Transit Company Limited (DMTCL).
      </p>
    </div>
  </section>
);
