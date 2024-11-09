import { useEffect, useState } from "react";
import { ThemeContext } from "../contexts";

const ThemeProvider = ({ children }) => {
  const [isDarkMode, setIsDarkMode] = useState(null); // Start as `null` to prevent flash
  const [isLoaded, setIsLoaded] = useState(false); // Prevent rendering until theme is determined

  // Function to toggle dark mode manually
  const toggleDarkMode = () => {
    setIsDarkMode((prevState) => !prevState);
  };

  // Set theme based on system preference once on initial load
  useEffect(() => {
    if (typeof window !== "undefined") {
      const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");

      // Set initial theme based on system preference
      setIsDarkMode(mediaQuery.matches);
      setIsLoaded(true); // Only render after setting the theme

      // Event listener for changes in system theme preferences
      const handleChange = (e) => {
        setIsDarkMode(e.matches);
      };

      mediaQuery.addEventListener("change", handleChange);

      // Cleanup event listener on unmount
      return () => mediaQuery.removeEventListener("change", handleChange);
    }
  }, []);

  // Prevent rendering until theme is determined
  if (!isLoaded) return null;

  return (
    <ThemeContext.Provider value={{ isDarkMode, toggleDarkMode }}>
      <div>{children}</div>
    </ThemeContext.Provider>
  );
};

export default ThemeProvider;
