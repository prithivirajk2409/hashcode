import { Outlet } from "react-router";
import { ToastContainer } from "react-toastify";
import { UserProvider } from "../src/context/userAuth";
import './App.css'
function App() {
  return (
    <>
      <UserProvider>
        <Outlet />
        <ToastContainer />
      </UserProvider>
    </>
  );
}

export default App;
