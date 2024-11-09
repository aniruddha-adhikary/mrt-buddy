import React from "react";
import { FeatureCard } from "./FeatureCard";

export const Features = () => {
  const features = [
    {
      title: "NFC Card Reader",
      description: "Instantly check your MRT Pass balance",
      icon: "📱",
    },
    {
      title: "Transaction History",
      description: "View your last 10 transactions",
      icon: "📊",
    },
    {
      title: "Pass Compatibility",
      description: "Works with both MRT Pass and Rapid Pass",
      icon: "💳",
    },
    {
      title: "Fare Calculator",
      description: "Calculate your fare with ease",
      icon: "🔢",
    }
  ];

  return (
    <section className="py-16 sm:py-24 bg-gray-50 dark:bg-[#121212]/[98%]">
      <div className="max-w-7xl mx-auto px-4 sm:px-6">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-8">
          {features.map((feature, index) => (
            <FeatureCard key={feature.title} feature={feature} index={index} />
          ))}
        </div>
      </div>
    </section>
  );
};
