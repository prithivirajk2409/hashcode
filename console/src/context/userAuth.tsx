import { createContext, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "react-toastify";
import React from "react";
import { loginAPI, signupAPI, refreshTokenAPI } from "../service/ApiService";

type UserContext = {
  signupUser: (email: string, username: string, password: string) => void;
  loginUser: (loginIdentifier: string, password: string) => void;
  logout: () => void;
  isLoggedIn: () => boolean;
};

type Props = { children: React.ReactNode };

const userContext = createContext<UserContext>({} as UserContext);

export const UserProvider = ({ children }: Props) => {
  const navigate = useNavigate();
  const [isReady, setIsReady] = useState(false);

  useEffect(() => {
    const refreshToken = async () => {
      if (localStorage.getItem("user") && localStorage.getItem("token")) {
        await refreshTokenAPI(localStorage.getItem("token") || "")
          .then((response) => {
            if (response) {
              const data = response?.data.data;
              if (data.refreshStatus == true) {
                localStorage.setItem("token", data.token);
                localStorage.setItem(
                  "user",
                  JSON.stringify({
                    userId: data.user.user_id,
                    userName: data.user.user_name,
                    emailId: data.user.email_id,
                  })
                );
              } else {
                localStorage.clear();
              }
            }
          })
          .catch((e) => {
            console.log(e);
            toast.warning("Something went wrong");
          });
      }
    };
    refreshToken();
    setIsReady(true);
  }, []);

  const signupUser = async (
    emailId: string,
    userName: string,
    password: string
  ) => {
    await signupAPI(emailId, userName, password)
      .then((response) => {
        if (response) {
          toast.success("Signup Success!");
          navigate("/login");
        }
      })
      .catch((e) => {
        console.log(e);
        toast.warning("Something went wrong");
      });
  };

  const loginUser = async (userName: string, password: string) => {
    await loginAPI(userName, password)
      .then((response) => {
        if (response) {
          const data = response?.data.data;
          localStorage.setItem(
            "user",
            JSON.stringify({
              userId: data.user.user_id,
              userName: data.user.user_name,
              emailId: data.user.email_id,
            })
          );
          localStorage.setItem("token", data.token);
          navigate("/");
        }
      })
      .catch((e) => {
        console.log(e);
        toast.warning("Something went wrong");
      });
  };

  const isLoggedIn = () => {
    const user = localStorage.getItem("user");
    return !!user;
  };

  const logout = () => {
    localStorage.clear();
    toast.success("Logout Success!");
    navigate("/");
  };

  return (
    <userContext.Provider value={{ loginUser, logout, isLoggedIn, signupUser }}>
      {isReady ? children : null}
    </userContext.Provider>
  );
};

export const useAuth = () => React.useContext(userContext);
