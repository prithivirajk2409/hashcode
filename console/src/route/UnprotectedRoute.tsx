import React from "react";
import { Navigate, useLocation } from "react-router-dom";
import { useAuth } from "../context/userAuth";

type Props = { children: React.ReactNode };

const UnProtectedRoute = ({ children }: Props) => {
    const location = useLocation();
    const { isLoggedIn } = useAuth();
    return isLoggedIn() ? (<Navigate to="/" state={{ from: location }} replace />) : (<>{children}</>);
};

export default UnProtectedRoute;
