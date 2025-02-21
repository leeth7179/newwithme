import { createTheme } from "@mui/material/styles";

const theme = createTheme({
  palette: {
    primary: {
      light: "#FFB998",
      main: "#FC8D4D", // Primary (Main Color)
      dark: "#5B2000",
      contrastText: "#FFFFFF"
    },
    secondary: {
      light: "#D8A58C",
      main: "#A65A3A",
      dark: "#3B1C0F",
      contrastText: "#FFFFFF"
    },
    tertiary: {
      light: "#F4E9E1",
      main: "#B89C98",
      dark: "#7C4F4F",
      contrastText: "#FFFFFF"
    },
    error: {
      light: "#F9DEDC",
      main: "#B3261E",
      dark: "#410E0B",
      contrastText: "#FFFFFF"
    },
    surface: {
      light: "#F5F5F5",
      main: "#121212",
      dark: "#000000",
      contrastText: "#FFFFFF"
    },
    background: {
      default: "#F5F5F5",
      paper: "#FFFFFF"
    },
    text: {
      primary: "#121212",
      secondary: "#3B1C0F",
      disabled: "rgba(0, 0, 0, 0.38)"
    },
    outline: {
      main: "#8F4F33"
    },
    success: { main: "#79BD9A", dark: "#235F9A" },
    warning: { main: "#F4CC6E", dark: "#FFC849" },
    danger: { main: "#E38AAE", dark: "#D23C79" },
    info: { main: "#65BBE0", dark: "#1E88E5" },
    disabledBg: { main: "#BDBDBD" }
  }
});

export default theme;
