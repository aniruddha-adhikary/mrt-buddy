import { createContext, useContext } from "react";

const ThemeContext = createContext("");

function useTheme() {
  return useContext(ThemeContext);
}

export { ThemeContext, useTheme };
