import React, { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";
import { useLocation } from "react-router-dom";
import refreshlogo from '../assets/refresh.png'; 
import { Resizable } from "re-resizable";
import Split from "react-split";
import Editor from "@monaco-editor/react";
import "./ProblemDetailPage.css";
import Badge from 'react-bootstrap/Badge';
import { problemDetailsAPI, problemInterpretAPI, submissionCheckAPI, submissionListAPI, problemSubmitAPI } from "../service/ApiService";

type TestResult = {
  input: string;
  output: string;
  expected: string;
  memory: string;
  runtime: string;
  status : string;
};

type TestReport = {
  acceptanceStatus : string | null;
  compilationOutput : string | null;
  runtimeOutput : string | null;
  lastExecutedInput : string| null;
  lastExecutedExpected : string | null;
  metadata : TestResult[] | null;
}

type Submission = {
  submissionId : number;
  status: string;
  submittedCode : string;
  runtime: number | null; 
  memory: number | null;
  language: string;
}

type ProblemDetail = {
  problemId : number;
  problemName : string;
  slug : string;
  content : string;
  difficulty : string;
  driverCode : any;
  templateCode : any;
  sampleTestCase : any;
  created : any;
  updated : any;
}

function formatStatusToTitleCase(status: string): string {
  return status
    .toLowerCase()
    .split('_')
    .map(word => word.charAt(0).toUpperCase() + word.slice(1))
    .join(' ');
}



const RunIcon: React.FC = () => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor" className="size-6">
    <path strokeLinecap="round" strokeLinejoin="round" d="M5.25 5.653c0-.856.917-1.398 1.667-.986l11.54 6.347a1.125 1.125 0 0 1 0 1.972l-11.54 6.347a1.125 1.125 0 0 1-1.667-.986V5.653Z" />
  </svg>
);

const CloudIcon: React.FC = () => (
  <svg xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24" strokeWidth="1.5" stroke="currentColor" className="size-6">
    <path strokeLinecap="round" strokeLinejoin="round" d="M12 9.75v6.75m0 0-3-3m3 3 3-3m-8.25 6a4.5 4.5 0 0 1-1.41-8.775 5.25 5.25 0 0 1 10.233-2.33 3 3 0 0 1 3.758 3.848A3.752 3.752 0 0 1 18 19.5H6.75Z" />
  </svg>
);




const ProblemDetailPage: React.FC = () => {
  // 425 325
  const location = useLocation();
  const navigate = useNavigate();
  const slug=location.pathname.split('/').pop();
  const [problem, setProblem] = useState<ProblemDetail | null>(null);
  const [activeTab, setActiveTab] = useState<"description" | "submission">("description");
  const [activeTestTab, setActiveTestTab] = useState<"testcase" | "testresult">("testcase");
  // const [topPanelHeight, setTopPanelHeight] = useState(425);
  // const [bottomPanelHeight, setBottomPanelHeight] = useState(325);
  const topPanelHeight = 425;
  const bottomPanelHeight = 325;
  const [activeTestCaseIndex, setActiveTestCaseIndex] = useState(0);
  const [activeTestResultIndex, setTestResultIndex] = useState(0);
  const [templateCodeMap, setTemplateCodeMap] = useState<Map<string, string>>(new Map());
  const [selectedLanguage, setSelectedLanguage] = useState<string>("cpp");
  const [code, setCode] = useState<string>("");
  const [sampleTestCase, setSampleTestCase] = useState<{ input: string; output: string }[]>([]);
  const [testResultBoxEmpty, setTestResultBoxEmpty] = useState(true);
  const [testResultLoading, setTestResultLoading] = useState(false);
  const [submissionResultLoading, setSubmissionResultLoading] =  useState(false);
  const [testResultFetchedStatus, setTestResultFetchedStatus] = useState<boolean>(false);
  const [testReport, setTestReport] = useState<TestReport | null>(null);
  const [submissions , setSubmissions] = useState<Submission[] | null>(null);
  const [showNewBadge, setShowNewBadge] = useState<boolean>(false);
 

  useEffect(() => {
    const callProblemDetailsAPI = async() =>{
      await problemDetailsAPI(slug!).then((response) => {
        if (response) {
          const data = response?.data.data;
          let temp: ProblemDetail = {
            problemId: data.problem_details.problem_id,
            problemName: data.problem_details.problem_name,
            slug: data.problem_details.slug,
            content: data.problem_details.content,
            difficulty: data.problem_details.difficulty,
            driverCode: data.problem_details.driver_code,
            templateCode: data.problem_details.template_code,
            sampleTestCase: data.problem_details.sample_test_case,
            created: data.problem_details.created,
            updated: data.problem_details.updated,
          };
          setProblem(temp);
          let parsedDriverCode =  JSON.parse(data.problem_details.template_code);
          let tempMap: Map<string, string> = new Map();
          Object.keys(parsedDriverCode).forEach((language) => {
            tempMap.set(language, parsedDriverCode[language]);
          });
          setTemplateCodeMap(tempMap);
          let selectedLanguageFromCacheOrDefault = localStorage.getItem("selectedLanguage") || tempMap.keys().next().value;
          setCode(localStorage.getItem(JSON.parse(localStorage.getItem('user') || '').userId + '-' + data.problem_details.problem_id + '-'+  selectedLanguageFromCacheOrDefault) || tempMap.get(selectedLanguageFromCacheOrDefault!)!);
          setSelectedLanguage(selectedLanguageFromCacheOrDefault!);
          setSampleTestCase(JSON.parse(data.problem_details.sample_test_case).map((testCase: { input: string; output: string }) => testCase));
        }
      });
    }
    callProblemDetailsAPI();
  }, []);


  // const handleResize = (e: any, direction: any, ref: any, delta: any) => {
  //   setTopPanelHeight(ref.offsetHeight);
  //   setBottomPanelHeight(750-ref.offsetHeight);
  // };

  // const handleBottomResize = (e: any, direction: any, ref: any, delta: any) => {
  //   setBottomPanelHeight(ref.offsetHeight);
  //   setTopPanelHeight(750-ref.offsetHeight);
  // };

  const handleLanguageChange = (
    event: React.ChangeEvent<HTMLSelectElement>
  ) => {
    localStorage.setItem("selectedLanguage", event.target.value.toLowerCase());
    setSelectedLanguage(event.target.value.toLowerCase());
    setCode(localStorage.getItem(JSON.parse(localStorage.getItem('user') || '').userId + '-' + problem?.problemId + '-'+  event.target.value.toLowerCase()) ||   templateCodeMap.get(event.target.value.toLowerCase())!);
  };

  const handleEditorChange = (value: string | undefined) => {
    localStorage.setItem(JSON.parse(localStorage.getItem('user') || '').userId + '-' + problem!.problemId + '-'+  selectedLanguage, value || '');
    setCode(value || '');
  };
  const handleTestCaseClick = (index: number) => {
    setActiveTestCaseIndex(index);
  };

  const handleTestResultClick = (index: number) => {
    setTestResultIndex(index);
  };

  

  const handleRunTestcase = async () => {
    setTestResultBoxEmpty(false);
    setTestResultLoading(true);
    setActiveTestTab("testresult");
    let jobId = -1;
    await problemInterpretAPI(
      problem!.problemId,
      code,
      selectedLanguage.toUpperCase()
    ).then((response) => {
      if (response) {
        const data = response?.data.data;
        jobId = data.jobId;
      }
    });
    const MAX_ITERATIONS = 20;
    if (jobId !== -1) {
      let it = 0;
      const interval = setInterval(async () => {
        if (it < MAX_ITERATIONS) {
          await submissionCheckAPI(jobId).then((response) => {
            if (response) {
              const data = response?.data.data;
              const curr: TestReport = {
                acceptanceStatus: null,
                compilationOutput: null,
                runtimeOutput : null,
                lastExecutedInput: null,
                lastExecutedExpected: null,
                metadata: null
              };
              if (data.status === "COMPLETED") {
                if(data.testReport.acceptanceStatus==='ACCEPTED' || data.testReport.acceptanceStatus==='WRONG_ANSWER'){
                  const metadata: TestResult[] = data.testReport.metadata.map(
                    (item: any) => ({
                      input: item.input,
                      output: item.output,
                      expected: item.expected,
                      memory: item.executionMemory,
                      runtime: item.executionTime,
                      status : item.acceptanceStatus
                    })
                  );
                  curr.metadata=metadata;
                }else if(data.testReport.acceptanceStatus==='COMPILATION_ERROR'){
                  curr.compilationOutput=data.testReport.compilationOutput;
                }else if(data.testReport.acceptanceStatus==='RUNTIME_ERROR'){
                  curr.runtimeOutput=data.testReport.runtimeOutput;
                  curr.lastExecutedInput=data.testReport.input;
                } else{
                  curr.lastExecutedInput=data.testReport.input;
                  curr.lastExecutedExpected=data.testReport.expected;
                }
                curr.acceptanceStatus=data.testReport.acceptanceStatus;
                setTestReport(curr);
                setTestResultFetchedStatus(true);
                setTestResultLoading(false);
                clearInterval(interval);
              }
            }
          });
          it++;
        } else {
          clearInterval(interval);
          setTestResultLoading(false);
        }
      }, 500);
    }
  };

  
  const handleSubmitTestcase = async () =>{
    setSubmissionResultLoading(true);
    let jobId = -1;
    await problemSubmitAPI(
      problem!.problemId,
      code,
      selectedLanguage.toUpperCase()
    ).then((response) => {
      if (response) {
        const data = response?.data.data;
        jobId = data.jobId;
      }
    });

    const MAX_ITERATIONS = 50;
    if (jobId !== -1) {
      let it = 0;
      const interval = setInterval(async () => {
        if (it < MAX_ITERATIONS) {
          await submissionCheckAPI(jobId).then((response) => {
            if (response) {
              const data = response?.data.data;
              if (data.status === "COMPLETED") {
                const submisionHistory: Submission[] = data.submissionHistory.map(
                  (item: any) => ({
                    submissionId : item.submission_id,
                    status: item.acceptance_status,
                    submittedCode : item.submitted_code,
                    runtime: item.execution_time,
                    memory: item.execution_memory,
                    language :item.programming_language,
                    
                  })
                );
                setSubmissions(submisionHistory);
                clearInterval(interval);
                setSubmissionResultLoading(false);
                setShowNewBadge(true);
              }
            }
          });
          it++;
        } else {
          clearInterval(interval);
          setSubmissionResultLoading(false);
        }
      }, 500);
    }
    setActiveTab("submission")
  }
  

  const handleSubmissionsOnClick = async(problemId : number) =>{
    //get submissions data
    await submissionListAPI(problemId).then((response) => {
      if (response) {
        const data = response?.data.data;
        const submissionsList: Submission[] = data.userSubmissions.map(
          (item: any) => ({
            submissionId : item.submission_id,
            status: item.acceptance_status,
            submittedCode : item.submitted_code,
            runtime: item.execution_time,
            memory: item.execution_memory,
            language :item.programming_language,
          }));
          setSubmissions(submissionsList);
      }
    });
    setActiveTab("submission")
  }
  

  const clearEditorContent = () =>{
    localStorage.removeItem(JSON.parse(localStorage.getItem('user') || '').userId + '-' + problem!.problemId + '-'+  selectedLanguage);
    setCode(templateCodeMap.get(selectedLanguage)!);
  }

  const handleNavigationToSubmissionPage = async (submissionId: number) => {
    navigate(`/submission/${submissionId}`);
  };

  if (!problem) {
    return <div className="problem-detail-page-loader-container">
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
    <section className="master-section">
      <Split
        className="split-container"
        sizes={[40, 60]}
        minSize={[300, 400]}
        direction="horizontal"
        gutterSize={7}
      >
        <div className="left-panel">
          <div className="ribbon">
            <button
              className={activeTab === "description" ? "active" : ""}
              onClick={() => setActiveTab("description")}
            >
              Description
            </button>
            <button
              className={activeTab === "submission" ? "active" : ""}
              onClick={() => handleSubmissionsOnClick(problem!.problemId)}
            >
              Submissions
            </button>
          </div>

          <div className="content">
            {activeTab === "description" && (
              <div>
                <div className="content-container" dangerouslySetInnerHTML={{ __html: JSON.parse(problem!.content) }}/>
              </div>
              
            )}
            {activeTab === "submission" && 
            (submissionResultLoading ? 
              (<div className="loader-container ">
                <div className="loader">
                  <svg className="circular" viewBox="25 25 50 50">
                    <circle className="path" cx="50" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10"/>
                  </svg>
                </div>
                </div>
             ): (
              <div className="submissions-table">
                <table>
                  <thead>
                    <tr>
                      <th>Status</th>
                      <th>Language</th>
                      <th>Runtime</th>
                      <th>Memory</th>
                    </tr>
                  </thead>
                  <tbody>
                    {submissions ? 
                      submissions.map((submission, index) => (
                        <tr key={index}>
                          <td className={submission.status === 'ACCEPTED' ? 'accepted' : 'error'} onClick={()=>handleNavigationToSubmissionPage(submission.submissionId)} style={{ cursor: 'pointer' }}  >
                            {formatStatusToTitleCase(submission.status)}
                            {index === 0 && showNewBadge && (
                              <Badge bg="primary" className="ms-2">New</Badge>
                            )}
                          </td>
                          <td>{formatStatusToTitleCase(submission.language)}</td>
                          <td>{submission.runtime !== null ? `${submission.runtime} ms` : 'N/A'}</td>
                          <td>{submission.memory !== null ? `${submission.memory} MB` : 'N/A'}</td>
                        </tr>
                      )) : <p>No Submissions to list</p>}
                  </tbody>
                </table>
              </div>
            ))}
          </div>
        </div>

        <div className="right-panel">
          <Resizable
            className="right-top-panel"
            size={{ height: topPanelHeight }}
            minHeight={150}
            maxHeight="80vh"
            enable={{ top: false, bottom: false }}
            // onResizeStop={handleResize}
          >
            <div className="ribbon">
              <div className="left-buttons">
                <select
                    value={selectedLanguage}
                    onChange={handleLanguageChange}
                    className="language-dropdown"
                  >
                    {Array.from(templateCodeMap.keys()).map((lang) => (
                      <option key={lang} value={lang}>
                        {lang.charAt(0).toUpperCase() + lang.slice(1).toLowerCase()}
                      </option>
                    ))}
                  </select>
              </div>
              <div className="right-buttons">
                <img src={refreshlogo} alt="letter" className="inline-block w-6 h-6 p-0.5 mr-[20px] cursor-pointer" onClick={() => clearEditorContent()}/>
              </div>
            </div>
            <Editor
              height="100%"
              width="100%"
              language={selectedLanguage}
              value={code}
              theme="vs-gray"
              
              onChange={handleEditorChange}
            />
          </Resizable>
          <Resizable
            className="right-bottom-panel"
            size={{ height: bottomPanelHeight }}
            // minHeight={30}
            // maxHeight="100vh"
            enable={{ top: false, bottom: false }}
            // onResizeStop={handleBottomResize}
          >
            <div className="ribbon">
              <div className="left-buttons">
                <button
                  className={activeTestTab === "testcase" ? "active" : ""}
                  onClick={() => setActiveTestTab("testcase")}
                >
                  Test Case
                </button>
                <button
                  className={activeTestTab === "testresult" ? "active" : ""}
                  onClick={() => setActiveTestTab("testresult")}
                >
                  Test Result
                </button>
              </div>
              <div className="right-buttons">
                <div className="button-container">
                  <button className="run-button" onClick={() => handleRunTestcase()}>
                    <RunIcon /><span className="ml-2">Run</span>
                  </button>
                  <button className="submit-button" onClick={() => handleSubmitTestcase()}>
                    <CloudIcon /><span className="ml-2">Submit</span>
                  </button>
                </div>
              </div>
            </div>

            <div className="testcases-container">
              {activeTestTab === "testcase" && (
                <div className="testcases">
                  <div className="testcase-buttons">
                    {sampleTestCase!.map((_: any, index: number) => (
                      <button
                        key={index}
                        className={`case-button ${
                          activeTestCaseIndex === index ? "active" : ""
                        }`}
                        onClick={() => handleTestCaseClick(index)}
                      >
                        Case {index + 1}
                      </button>
                    ))}
                  </div>
                  <div className="testcase-details">
                      <div>
                        <h4>Input</h4>
                        <div className="test-parameter-container">
                          {/* <pre>abcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfnabcdefghijklmnojsalkfnsnfnnfsnfnfnnfn</pre> */}
                          <pre>{sampleTestCase![activeTestCaseIndex].input}</pre>
                        </div>
                    </div>
                  </div>
                </div>
              )}
              {activeTestTab === "testresult" && (
                <div className="testresult">
                  {testResultBoxEmpty ? (
                    <p>No test result data available.</p>
                  ) : (testResultLoading ? (
                    <div className="loader-container ">
                <div className="loader">
                  <svg className="circular" viewBox="25 25 50 50">
                    <circle className="path" cx="50" cy="50" r="20" fill="none" stroke-width="2" stroke-miterlimit="10"/>
                  </svg>
                </div>
              </div>
                  ) : (testResultFetchedStatus? 
                    (testReport?.acceptanceStatus==='COMPILATION_ERROR' ? 
                      (<div className="compile-error-container">
                        <h2 className="test-result-title-not-accepted">Compilation Error</h2>
                        <div className="compile-error-details">
                          <pre>{testReport.compilationOutput}</pre>
                        </div>
                      </div>) :
                      (testReport?.acceptanceStatus==='ACCEPTED' ||testReport?.acceptanceStatus==='WRONG_ANSWER')  ? 
                      (<>
                        <div className="testresult-ribbon">
                          <div className = "left-case-details">
                          <div className="testcase-buttons">
                              {testReport.metadata?.map((_: TestResult, index: number) => (
                                <button
                                  key={index}
                                  className={`case-button ${
                                    activeTestResultIndex === index ? "active" : ""
                                  }`}
                                  onClick={() => handleTestResultClick(index)}
                                >
                                <span className={`status-dot ${ testReport.metadata && testReport.metadata[activeTestResultIndex].status && testReport.metadata[index].status === "ACCEPTED" ? "accepted-dot" : "wrong-answer-dot"}`}></span>
                                  Case {index + 1}
                                </button>
                              ))}
                            </div>
                          </div>
                          <div className="right-test-result">
                          <h2 className={`test-result-title-${
                                  testReport?.acceptanceStatus === 'ACCEPTED' ? "accepted" : "not-accepted"
                                }`}>{formatStatusToTitleCase(testReport?.acceptanceStatus)}</h2>
                          </div>
                          
                        </div>
                        <div className="testcase-details">
                            <div>
                              <h4>Input</h4>
                              <div className="test-parameter-container">
                                <pre>{testReport.metadata && testReport.metadata[activeTestResultIndex].input ? testReport.metadata[activeTestResultIndex].input : "No Input"}</pre>
                              </div>
                            </div>
                            <div>
                              <h4>Output</h4>
                              <div className="test-parameter-container">
                                <pre>{testReport.metadata && testReport.metadata[activeTestResultIndex].output ? testReport.metadata[activeTestResultIndex].output : "No Output"}</pre>
                              </div>
                            </div>
                            <div>
                              <h4>Expected</h4>
                              <div className="test-parameter-container">
                                <pre>{testReport.metadata && testReport.metadata[activeTestResultIndex].expected ? testReport.metadata[activeTestResultIndex].expected : "No Expected"}</pre>
                              </div>
                            </div>
                        </div>
                        <div className="execution-stat">
                            <p>
                              Runtime:{" "}
                              {testReport.metadata && testReport.metadata[activeTestResultIndex].runtime ? testReport.metadata[activeTestResultIndex].runtime : ""} ms |
                              Memory:{" "}
                              {testReport.metadata && testReport.metadata[activeTestResultIndex].memory ? testReport.metadata[activeTestResultIndex].memory : ""} MB
                            </p>
                        </div>
                      </>) :
                      (<div className="runtime-error-container">
                        <p className="test-result-title-not-accepted">{testReport?.acceptanceStatus && formatStatusToTitleCase(testReport.acceptanceStatus)}</p>
                        <div className="runtime-error-details">
                          {testReport?.runtimeOutput && testReport.runtimeOutput.trim() !=="" ? 
                            <div className="compile-error-details">
                              <pre>{testReport?.runtimeOutput}</pre>
                            </div>: <></>} 
                        
                          <h4>Last Executed Input</h4>
                          <div className="test-parameter-container">
                            <pre>{testReport?.lastExecutedInput}</pre>
                          </div>
                        </div>
                        
                      </div>) 
                    ) 
                     : (
                    <p>Something went wrong</p>
                  )))}
                </div>
              )}
            </div>
          </Resizable>
          
        </div>
      </Split>
     
    </section>
  );
};

export default ProblemDetailPage;
