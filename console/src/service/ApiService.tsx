import axios from "axios";
import { handleError } from "../utility/ErrorHandler";

const API_BASE_URL = "http://localhost:8080/api";
// const API_BASE_URL  = "https://hashcode.fun/api"

axios.defaults.headers.common["Content-Type"] = "application/json";
axios.defaults.headers.common["x-service-name"] = "api";

export const loginAPI = async (userName: string, password: string) => {
  try {
    const data = await axios.post(API_BASE_URL + "/auth" + "/login", {
      user_name: userName,
      password: password,
    });
    return data;
  } catch (error) {
    handleError(error);
  }
};

export const signupAPI = async (
  emailId: string,
  userName: string,
  password: string
) => {
  try {
    const data = await axios.post(API_BASE_URL + "/auth" + "/signup", {
      email_id: emailId,
      user_name: userName,
      password: password,
    });
    return data;
  } catch (error) {
    handleError(error);
  }
};

export const refreshTokenAPI = async (token: string) => {
  try {
    const data = await axios.post(API_BASE_URL + "/auth" + "/refresh/token", {
      bearerToken: token,
    });
    return data;
  } catch (error) {
    handleError(error);
  }
};

export const problemListApi = async (pageNumber: number) => {
  try {
    const headers: { [key: string]: string } = {};
    if (localStorage.getItem("token")) {
      headers["Authorization"] = "Bearer " + localStorage.getItem("token");
    }
    const data = await axios.get(
      API_BASE_URL + "/problem" + `/list/${pageNumber}`,
      { headers: headers }
    );
    return data;
  } catch (error) {
    handleError(error);
  }
};

export const problemDetailsAPI = async (slug: string) => {
  try {
    const headers: { [key: string]: string } = {};
    if (localStorage.getItem("token")) {
      headers["Authorization"] = "Bearer " + localStorage.getItem("token");
    }
    const data = await axios.get(API_BASE_URL + "/problem" + `/${slug}/info`, {
      headers: headers,
    });
    return data;
  } catch (error) {
    handleError(error);
  }
};

export const submissionListAPI = async (problemId: number) => {
  try {
    const headers: { [key: string]: string } = {};
    if (localStorage.getItem("token")) {
      headers["Authorization"] = "Bearer " + localStorage.getItem("token");
    }
    const data = await axios.get(
      API_BASE_URL + "/submission" + `/${problemId}/list`,
      { headers: headers }
    );
    return data;
  } catch (error) {
    handleError(error);
  }
};

export const problemInterpretAPI = async (
  problemId: number,
  typedCode: string,
  programmingLanguage: string
) => {
  try {
    const headers: { [key: string]: string } = {};
    if (localStorage.getItem("token")) {
      headers["Authorization"] = "Bearer " + localStorage.getItem("token");
    }
    const data = await axios.post(
      API_BASE_URL + "/problem" + `/${problemId}/interpret`,
      {
        typed_code: typedCode,
        programming_language: programmingLanguage,
      },
      { headers: headers }
    );
    return data;
  } catch (error) {
    handleError(error);
  }
};

export const problemSubmitAPI = async (
  problemId: number,
  typedCode: string,
  programmingLanguage: string
) => {
  try {
    const headers: { [key: string]: string } = {};
    if (localStorage.getItem("token")) {
      headers["Authorization"] = "Bearer " + localStorage.getItem("token");
    }
    const data = await axios.post(
      API_BASE_URL + "/problem" + `/${problemId}/submit`,
      {
        typed_code: typedCode,
        programming_language: programmingLanguage,
      },
      { headers: headers }
    );
    return data;
  } catch (error) {
    handleError(error);
  }
};

export const submissionCheckAPI = async (jobId: number) => {
  try {
    const headers: { [key: string]: string } = {};
    if (localStorage.getItem("token")) {
      headers["Authorization"] = "Bearer " + localStorage.getItem("token");
    }
    const data = await axios.get(
      API_BASE_URL + "/submission" + `/${jobId}/check`,
      { headers: headers }
    );
    return data;
  } catch (error) {
    handleError(error);
  }
};

export const submissionDetailsAPI = async (submissionId: number) => {
  try {
    const headers: { [key: string]: string } = {};
    if (localStorage.getItem("token")) {
      headers["Authorization"] = "Bearer " + localStorage.getItem("token");
    }
    const data = await axios.get(
      API_BASE_URL + "/submission" + `/${submissionId}/info`,
      { headers: headers }
    );
    return data;
  } catch (error) {
    handleError(error);
  }
};
