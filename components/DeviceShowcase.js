import React from "react";
import { Iphone15Pro } from "./Iphone15Pro";

export const DeviceShowcase = () => {
  const devices = ["/1.jpeg", "/2.jpeg"];

  return (
    <section className="py-4 sm:py-8 md:py-12 overflow-hidden dark:bg-[#121212]">
      <div className="max-w-[1400px] mx-auto px-4 sm:px-6">
        <div className="flex flex-col md:flex-row justify-center items-center gap-6 md:gap-8">
          {devices.map((device, index) => (
            <div key={index} className="w-full px-4 sm:px-0">
              <div className="relative max-w-[280px] sm:max-w-[400px] md:max-w-[500px] lg:max-w-[652px] mx-auto">
                <Iphone15Pro
                  width="100%"
                  height="652"
                  src={device}
                  className="w-full transform-gpu hover:scale-102 transition-transform duration-300 ease-in-out"
                  style={{
                    maxWidth: "100%",
                    height: "auto",
                    aspectRatio: "408/652",
                  }}
                />
              </div>
            </div>
          ))}
        </div>
      </div>
    </section>
  );
};
