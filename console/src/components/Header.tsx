import { Link } from "react-router-dom";
import logo from '../assets/logo.png';

import { useAuth } from "../context/userAuth";

const Header = () => {
  const { isLoggedIn, logout } = useAuth();
  return (
    <header className="bg-gray-900 text-white">
      <nav className="max-w-7xl mx-auto p-4 flex justify-between items-center">
        <div className="flex items-center">
          <Link to="/" className="text-2xl font-bold-itali">
            <img
              src={logo}
              alt="letter"
              className="inline-block w-6 h-6"
            />
            ashcode
          </Link>
          <Link
            to="/problems"
            className="hover:text-yellow-400 text-left mr-20 ml-20"
          >
            Problems
          </Link>
        </div>

        <div className="flex justify-end space-x-4 ">
          {isLoggedIn() ? (
            <>
              <a>Welcome, {JSON.parse(localStorage.getItem('user')!).userName}</a>
              <a
                onClick={logout}
                className="hover:text-yellow-400 cursor-pointer"
              >
                Logout
              </a>
            </>
          ) : (
            <>
              <Link to="/login" className="hover:text-darkBlue">
                Login
              </Link>
              <Link to="/signup" className="hover:text-darkBlue">
                Signup
              </Link>
            </>
          )}
        </div>
      </nav>
    </header>
  );
};

export default Header;
