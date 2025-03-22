import { createBrowserRouter } from "react-router-dom";
import App from "../App";
import LoginPage from "../pages/LoginPage";
import SignupPage from "../pages/SignupPage";
import UnProtectedRoute from "./UnprotectedRoute";
import ProblemsPage from "../pages/ProblemsPage";
import ProblemDetailPage from "../pages/ProblemDetailPage";
import ProtectedRoute from "./ProtectedRoute";
import HomePage from "../pages/HomePage";
import SubmissionDetailPage from "../pages/SubmissionDetailPage";
import Header from "../components/Header";
import Footer from "../components/Footer";

export const router = createBrowserRouter([
  {
    path: "/",
    element: <App />,
    children: [
      {
        // path: "/",
        index: true,
        element: (
          <>
            <Header />
            <HomePage />
            <Footer />
          </>
        ),
      },
      {
        path: "login",
        element: (
          <UnProtectedRoute>
            <Header />
            <LoginPage />
            <Footer />
          </UnProtectedRoute>
        ),
      },
      {
        path: "signup",
        element: (
          <UnProtectedRoute>
            <Header />
            <SignupPage />
            <Footer />
          </UnProtectedRoute>
        ),
      },
      {
        path: "problems",
        element: (
          <ProtectedRoute>
            <Header />
            <ProblemsPage />
            <Footer />
          </ProtectedRoute>
        ),
      },
      {
        path: "problems/:slug",
        element: (
          <ProtectedRoute>
            <Header />
            <ProblemDetailPage />
          </ProtectedRoute>
        ),
      },
      {
        path: "submission/:submissionId",
        element : (
          <ProtectedRoute>
          <Header />
          <SubmissionDetailPage/>
          <Footer />
          </ProtectedRoute>
        ),
      }
    ],
  },
]);
