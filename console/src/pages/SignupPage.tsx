import * as Yup from "yup";
import { yupResolver } from "@hookform/resolvers/yup";
import { useAuth } from "../context/userAuth";
import { useForm } from "react-hook-form";
import { Link } from "react-router-dom";


type RegisterFormInput = {
  email: string;
  userName: string;
  password: string;
  confirmPassword : string
};

const validation = Yup.object().shape({
  email: Yup.string().required("Email is required").matches(/^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{3,}$/, "Email Id is invalid"),
  userName: Yup.string().required("Username is required"),
  password: Yup.string().required("Password is required")
                        .min(8, "Password must be at least 8 characters")
                        .max(12, "Password must not exceed 12 characters")
                        .matches(/[A-Z]/, "Password must contain at least one uppercase letter")
                        .matches(/[a-z]/, "Password must contain at least one lowercase letter")
                        .matches(/[\W_]/, "Password must contain at least one special character"),
  confirmPassword : Yup.string().oneOf([Yup.ref('password')], "Passwords must match").required("Confirm Password is required"),
});

const SignupPage = () => {
  const { signupUser } = useAuth();
  const {
    register,
    handleSubmit,
    formState: { errors },
  } = useForm<RegisterFormInput>({ resolver: yupResolver(validation) });

  const handleSignup = (form: RegisterFormInput) => {
    signupUser(form.email, form.userName, form.password);
  };
  return (
    <section className="bg-gray-50 dark:bg-gray-900">
      <div className="flex flex-col items-center justify-center px-6 py-8 mx-auto md:h-screen lg:py-0">
        <div className="w-full bg-white rounded-lg shadow dark:border md:mb-20 sm:max-w-md xl:p-0 dark:bg-gray-800 dark:border-gray-700">
          <div className="p-6 space-y-4 md:space-y-6 sm:p-8">
            <h1 className="text-xl font-bold leading-tight tracking-tight text-gray-900 md:text-2xl dark:text-white">
              Sign in to your account
            </h1>
            <form
              className="space-y-4 md:space-y-6"
              onSubmit={handleSubmit(handleSignup)}
            >
              <div>
                <label
                  htmlFor="email"
                  className="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
                >
                  Email
                </label>
                <input
                  type="text"
                  id="email"
                  className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                  placeholder="Email"
                  {...register("email")}
                />
                {errors.email ? (
                  <p className="text-red-500 border-gray-300 sm:text-sm rounded-lg">{errors.email.message}</p>
                ) : (
                  ""
                )}
              </div>
              <div>
                <label
                  htmlFor="username"
                  className="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
                >
                  Username
                </label>
                <input
                  type="text"
                  id="username"
                  className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                  placeholder="Username"
                  {...register("userName")}
                />
                {errors.userName ? (
                  <p className="text-red-500 border-gray-300 sm:text-sm rounded-lg">{errors.userName.message}</p>
                ) : (
                  ""
                )}
              </div>
              <div>
                <label
                  htmlFor="password"
                  className="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
                >
                  Password
                </label>
                <input
                  type="password"
                  id="password"
                  placeholder="••••••••"
                  className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                  {...register("password")}
                />
                {errors.password ? (
                  <p className="text-red-500 border-gray-300 sm:text-sm rounded-lg">{errors.password.message}</p>
                ) : (
                  ""
                )}
              </div>
              <div>
                <label
                  htmlFor="confirmPassword"
                  className="block mb-2 text-sm font-medium text-gray-900 dark:text-white"
                >
                  Confirm Password
                </label>
                <input
                  type="text"
                  id="confirmPassword"
                  placeholder="Confirm Password"
                  className="bg-gray-50 border border-gray-300 text-gray-900 sm:text-sm rounded-lg focus:ring-primary-600 focus:border-primary-600 block w-full p-2.5 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:ring-blue-500 dark:focus:border-blue-500"
                  {...register("confirmPassword")}
                />
                {errors.confirmPassword ? (
                  <p className="text-red-500 border-gray-300 sm:text-sm rounded-lg">{errors.confirmPassword.message}</p>
                ) : (
                  ""
                )}
              </div>
              {/* <div className="flex items-center justify-between">
                <a
                  href="#"
                  className="text-sm text-white font-medium text-primary-600 hover:underline dark:text-primary-500"
                >
                  Forgot password?
                </a>
              </div> */}
              <button
                type="submit"
                className="w-full text-white bg-gray-900 hover:bg-primary-700 focus:ring-4 focus:outline-none focus:ring-primary-300 font-medium rounded-lg text-sm px-5 py-2.5 text-center dark:bg-primary-600 dark:hover:bg-primary-700 dark:focus:ring-primary-800"
              >
                Sign Up
              </button>
              <p className="text-sm font-light text-gray-500 dark:text-gray-400">
                Have an account? {" "}
                <Link to="/login" className="font-medium text-primary-600 hover:underline dark:text-primary-500">
                Login
                </Link>
              </p>
            </form>
          </div>
        </div>
      </div>
    </section>
  );
};

export default SignupPage;
