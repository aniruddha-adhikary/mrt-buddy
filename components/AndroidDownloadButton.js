import { motion } from "framer-motion";
import React from "react";
import { LogoAndroid } from "./LogoAndroid";

const AndroidDownloadButton = ({ onClick, isClicked }) => {
  return (
    <motion.button
      onClick={onClick}
      className="download-button-android bg-black text-white px-2 py-4 rounded-lg flex items-center justify-center gap-2 dark:border dark:border-gray-400"
      style={{
        width: "150px",
        height: "50px",
      }}
    >
      <LogoAndroid className="w-10 h-auto pr-1" />
      <div className="flex flex-col items-start">
        <span className="text-[10px] leading-none opacity-80">
          Download APK
        </span>
        <span className="text-[20px] font-medium leading-tight">Android</span>
      </div>
    </motion.button>
  );
};

export default AndroidDownloadButton;
