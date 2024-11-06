import React, { useState } from 'react';
import { motion } from 'framer-motion';

const AnimatedDownloadButton = ({ platform, icon: Icon, initialText, changeText, onClick, isAnimating }) => {
  const [isClicked, setIsClicked] = useState(false);

  const handleClick = () => {
    if (!isAnimating) {
      setIsClicked(true);
      onClick?.();
      setTimeout(() => {
        setIsClicked(false);
      }, 2000);
    }
  };

  return (
    <motion.button
      onClick={handleClick}
      className={`download-button ${isClicked ? 'success' : ''}`}
      whileHover={{ scale: 1.05 }}
      whileTap={{ scale: 0.95 }}
    >
      <Icon className="w-5 h-5" />
      <span>{isClicked ? changeText : initialText}</span>
    </motion.button>
  );
};

export default AnimatedDownloadButton;
