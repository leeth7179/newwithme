import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.jsx";
import { BrowserRouter } from "react-router-dom";
import { Provider } from "react-redux";
import { store, persistor } from "./redux/store"; // Redux 스토어 및 Persistor 가져오기
import { PersistGate } from "redux-persist/integration/react"; // PersistGate 가져오기
//mui custom
import { ThemeProvider } from "@mui/material/styles";
import theme from "./assets/css/mui/theme";
// import "./assets/css/reset/reset.css";
// import "./assets/css/reset/common.css";

createRoot(document.getElementById("root")).render(
  <Provider store={store}>
    <ThemeProvider theme={theme}>
      <PersistGate loading={null} persistor={persistor}>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </PersistGate>
    </ThemeProvider>
  </Provider>
);

persistor.subscribe(() => {
  console.log("main.jsx Persistor 상태:", store.getState());
});
