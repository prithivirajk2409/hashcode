import React, { useEffect, useState } from "react";
import { Light as SyntaxHighlighter } from "react-syntax-highlighter";
import { vs } from "react-syntax-highlighter/dist/esm/styles/hljs";
// import "react-syntax-highlighter/dist/esm/styles/hljs/vs.css";
import "./SubmissionDetailPage.css";
import { formatDistanceToNow, parseISO } from "date-fns";
import { useNavigate } from "react-router-dom";
import { submissionDetailsAPI } from "../service/ApiService";
import { useLocation } from "react-router-dom";

interface SubmissionDetail {
  submissionId: number;
  problemId: number;
  userId: number;
  problemName: string;
  slug: string;
  programmingLanguage: string;
  acceptanceStatus: string;
  submittedCode: string;
  executionTime: number;
  executionMemory: number;
  executionJobId: number;
  metadata: any;
  created: any;
  updated: any;
}

function formatStatusToTitleCase(status: string): string {
  return status
    .toLowerCase()
    .split("_")
    .map((word) => word.charAt(0).toUpperCase() + word.slice(1))
    .join(" ");
}

const SubmissionDetailPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(true);
  const navigate = useNavigate();
  const [submissionDetails, setSubmissionDetails] =
    useState<SubmissionDetail | null>();
  const location = useLocation();
  const submissionId = Number(location.pathname.split("/").pop());

  useEffect(() => {
    const fetchProblems = async () => {
      setLoading(true);
      await submissionDetailsAPI(submissionId).then((response) => {
        if (response) {
          let data = response?.data.data;
          let temp: SubmissionDetail = {
            submissionId: data.submission_details.submission_id,
            problemId: data.submission_details.problem_id,
            problemName: data.problem_name,
            slug: data.slug,
            userId: data.submission_details.user_id,
            programmingLanguage: data.submission_details.programming_language,
            acceptanceStatus: data.submission_details.acceptance_status,
            submittedCode: data.submission_details.submitted_code,
            executionTime: data.submission_details.execution_time,
            executionMemory: data.submission_details.execution_memory,
            executionJobId: data.submission_details.execution_job_id,
            metadata: JSON.parse(data.submission_details.metadata),
            created: data.submission_details.created,
            updated: data.submission_details.updated,
          };
          setSubmissionDetails(temp);
          setLoading(false);
        }
      });
    };
    fetchProblems();
  }, [submissionId]);

  const handleNavigationToProblemPage = async (slug: string) => {
    navigate(`/problems/${slug}`);
  };

  if (!submissionDetails) {
    return <div className="submission-page-loader-container">
      <div className="loader-container ">
        <div className="loader">
          <svg className="circular" viewBox="25 25 50 50">
            <circle className="path" cx="50" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10"/>
              </svg>
          </div>
        </div>
      </div>;
  }

  return (
    <div className="page-container">
      {loading ? (
        <div className="text-center"></div>
      ) : (
        <>
          <div className="problem-title">
            <a
              onClick={() =>
                handleNavigationToProblemPage(submissionDetails!.slug)
              }
            >
              {submissionDetails?.problemName}
            </a>
          </div>
          {submissionDetails?.acceptanceStatus === "COMPILATION_ERROR" ? (
            <></>
          ) : (
            <div className="test-case-status">
              <p>
                <strong>
                  {submissionDetails?.metadata.processedTestCases}
                </strong>{" "}
                / {submissionDetails?.metadata.totalTestCases} test cases
                passed.
              </p>
            </div>
          )}

          <div className="execution-status-container">
            <div className="left-content">
              <h2
                className={`status-${
                  submissionDetails?.acceptanceStatus === "ACCEPTED"
                    ? "accepted"
                    : "not-accepted"
                }`}
              >
                {formatStatusToTitleCase(submissionDetails!.acceptanceStatus)}
              </h2>
            </div>
            <div className="right-content">
              {submissionDetails?.acceptanceStatus === "ACCEPTED" && (
                <div className="execution-stat">
                  <p>
                    Runtime: {submissionDetails!.executionTime} ms | Memory:{" "}
                    {submissionDetails!.executionMemory} MB
                  </p>
                </div>
              )}
            </div>
          </div>

          {submissionDetails?.acceptanceStatus === "COMPILATION_ERROR" && (
            <div className="error-container">
              <div className="error-details">
                <pre>{submissionDetails?.metadata.compilationOutput}</pre>
              </div>
            </div>
          )}

          {submissionDetails?.acceptanceStatus === "RUNTIME_ERROR" && (
            <>
              {submissionDetails.metadata.runtimeOutput &&
              submissionDetails.metadata.runtimeOutput.trim() !== "" ? (
                <div className="error-container">
                  <div className="error-details">
                    <pre>{submissionDetails?.metadata.runtimeOutput}</pre>
                  </div>
                </div>
              ) : (
                <></>
              )}
              <div className="test-case-container">
                <div>
                  <h4>Last Executed Input</h4>
                  <div className="test-case">
                    <pre>{submissionDetails?.metadata.input}</pre>
                  </div>
                </div>
              </div>
            </>
          )}

          {submissionDetails?.acceptanceStatus === "WRONG_ANSWER" && (
            <div className="test-case-container">
              <h4>Input</h4>
              <div className="test-case">
                <pre>{submissionDetails?.metadata.input}</pre>
              </div>
              <h4>Output</h4>
              <div className="test-case">
                <pre>{submissionDetails?.metadata.output ? submissionDetails?.metadata.output : "No Output"}</pre>
              </div>
              <h4>Expected</h4>
              <div className="test-case">
                <pre>{submissionDetails?.metadata.expected}</pre>
              </div>
            </div>
          )}

          {(submissionDetails?.acceptanceStatus === "TIME_LIMIT_EXCEEDED" ||
            submissionDetails?.acceptanceStatus === "MEMORY_LIMIT_EXCEEDED" ||
            submissionDetails?.acceptanceStatus === "UNKNOWN_ERROR") && (
            <div className="test-case-container">
              <h4>Last Executed Input</h4>
              <div className="test-case">
                <pre>{submissionDetails?.metadata.input}</pre>
              </div>
            </div>
          )}

          <div className="submitted-code-section">
            <h2 className="text-lg font-bold">
              Submitted Code:{" "}
              {formatDistanceToNow(
                new Date(
                  parseISO(submissionDetails?.created).getTime() +
                    5.5 * 60 * 60 * 1000
                ),
                { addSuffix: true }
              )}
            </h2>
            <p className="code-language">
              Language: {formatStatusToTitleCase(submissionDetails!.programmingLanguage)}
            </p>
            <div className="code-block">
              <SyntaxHighlighter
                language="python"
                style={vs}
                showLineNumbers={true}
                customStyle={{ backgroundColor: "#f4f4f4" }}
              >
                {submissionDetails?.submittedCode!}
              </SyntaxHighlighter>
            </div>
          </div>
        </>
      )}
    </div>
  );
};

export default SubmissionDetailPage;
