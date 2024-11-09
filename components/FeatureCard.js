import { motion } from "framer-motion";
import React from "react";

export const FeatureCard = ({ feature, index }) => (
  <motion.div
    className="feature-card"
    whileHover={{ scale: 1.02 }}
    initial={{ opacity: 0, y: 20 }}
    animate={{ opacity: 1, y: 0 }}
    transition={{ delay: index * 0.1 }}
  >
    <div className="relative bg-white p-6 rounded-lg dark:bg-[#121212]/[70%]">
      <span className="text-4xl mb-4 block">{feature.icon}</span>
      <h3 className="text-xl font-semibold mb-2 dark:text-white">
        {feature.title}
      </h3>
      <p className="text-gray-600 dark:text-gray-100">{feature.description}</p>
    </div>
  </motion.div>
);
