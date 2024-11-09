import { useEffect, useState } from "react";
import { ThemeContext } from "../contexts";

const ThemeProvider = ({ children }) => {
  const [isDarkMode, setIsDarkMode] = useState(false);

  // Function to toggle dark mode manually
  const toggleDarkMode = () => {
    setIsDarkMode((prevState) => !prevState);
  };

  // Set theme based on system preference on component mount
  useEffect(() => {
    const mediaQuery = window.matchMedia("(prefers-color-scheme: dark)");

    // Initial check
    setIsDarkMode(mediaQuery.matches);

    // Event listener for system theme changes
    const handleChange = (e) => {
      setIsDarkMode(e.matches);
    };

    mediaQuery.addEventListener("change", handleChange);

    // Clean up the event listener on component unmount
    return () => mediaQuery.removeEventListener("change", handleChange);
  }, []);

  return (
    <ThemeContext.Provider value={{ isDarkMode, toggleDarkMode }}>
      {children}
    </ThemeContext.Provider>
  );
};

export default ThemeProvider;
