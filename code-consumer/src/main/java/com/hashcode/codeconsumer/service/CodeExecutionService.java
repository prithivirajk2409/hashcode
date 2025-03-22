package com.hashcode.codeconsumer.service;

import java.security.Timestamp;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.hashcode.dao.CodeExecutionJobsDAO;
import com.hashcode.dao.ProblemsDAO;
import com.hashcode.dao.UserSubmissionsDAO;
import com.hashcode.entity.CodeExecutionRequest;
import com.hashcode.entity.HCCodeExecutionJobs;
import com.hashcode.entity.HCProblems;
import com.hashcode.entity.HCUserSubmissions;
import com.hashcode.enums.AcceptanceStatus;
import com.hashcode.enums.CodeExecutionJobStatus;
import com.hashcode.enums.DatabaseProviderIdentifier;
import com.hashcode.enums.ExecutionType;
import com.hashcode.enums.ProgrammingLanguage;
import com.hashcode.dao.TestCasesDAO;
import com.hashcode.mybatis.DatabaseUtility;
import com.hashcode.utils.CommonUtilities;
import com.hashcode.utils.Constants;

import io.micrometer.common.util.StringUtils;

@Service
public class CodeExecutionService {

	@Autowired
	DockerPoolManagerService dockerPool;

	private static final Logger logger = LogManager.getLogger(CodeExecutionService.class);

	private String getFinalCode(ProgrammingLanguage programmingLanguage, String typedCode, int problemId) {
		String finalCode = null;
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			HCProblems problem = new ProblemsDAO(sqlSession).getProblemById(problemId);
			String driverCode = new JSONObject(problem.getDriverCode())
					.getString(programmingLanguage.toString().toLowerCase());

			switch (programmingLanguage) {
			case JAVA:
				finalCode = Constants.JAVA_MACROS + "\n" + typedCode + "\n" + driverCode;
				break;
			case CPP:
				finalCode = Constants.CPP_MACROS + "\n" + typedCode + "\n" + driverCode;
				break;
			case PYTHON:
				finalCode = Constants.PYTHON_MACROS + "\n" + typedCode + "\n" + driverCode;
				break;
			default:
				break;
			}
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return finalCode;

	}

	public void processCodeExecutionRequestForTest(CodeExecutionRequest request) throws Exception {
		logger.info("Receieved Payload : {}", request);
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			CodeExecutionJobsDAO codeExecutionJobsDAO = new CodeExecutionJobsDAO(sqlSession);
			HCCodeExecutionJobs job = new CodeExecutionJobsDAO(sqlSession)
					.getExecutionJobById(request.getExecutionJobId());
			job.setStatus(CodeExecutionJobStatus.STARTED.getStatus());
			codeExecutionJobsDAO.update(job);

			String sampleTestCase = new ProblemsDAO(sqlSession).getProblemById(request.getProblemId())
					.getSampleTestCase();
			String finalCode = getFinalCode(request.getProgrammingLanguage(), request.getSubmittedCode(),
					request.getProblemId());

			Map<String, Object> metadata = dockerPool.executeCodeForTest(
					request.getProgrammingLanguage().toString().toLowerCase(), finalCode, sampleTestCase);

			job.setStatus(CodeExecutionJobStatus.COMPLETED.getStatus());
			job.setMetadata(CommonUtilities.getObjectMapper().writeValueAsString(metadata));
			job.setSubmissionId(null);
			codeExecutionJobsDAO.update(job);

//			logger.info("MetaData After Processing : {}", metadata);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
			throw e;
		}

	}

	public void processCodeExecutionRequestForSubmit(CodeExecutionRequest request) throws Exception {
		logger.info("Receieved Payload : {}", request);
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			CodeExecutionJobsDAO codeExecutionJobsDAO = new CodeExecutionJobsDAO(sqlSession);
			HCCodeExecutionJobs job = new CodeExecutionJobsDAO(sqlSession)
					.getExecutionJobById(request.getExecutionJobId());
			job.setStatus(CodeExecutionJobStatus.STARTED.getStatus());
			codeExecutionJobsDAO.update(job);

			String testCase = new TestCasesDAO(sqlSession).getTestCasesForProblemId(request.getProblemId())
					.getTestCase();
			String finalCode = getFinalCode(request.getProgrammingLanguage(), request.getSubmittedCode(),
					request.getProblemId());

			Map<String, Object> result = dockerPool.executeCodeForSubmit(
					request.getProgrammingLanguage().toString().toLowerCase(), finalCode, testCase);

			AcceptanceStatus acceptanceStatus = AcceptanceStatus.valueOf(result.get("acceptanceStatus").toString());
			HCUserSubmissions userSubmission = new HCUserSubmissions();
			userSubmission.setUserId(request.getUserId());
			userSubmission.setProblemId(request.getProblemId());
			userSubmission.setProgrammingLanguage(request.getProgrammingLanguage());
			userSubmission.setAcceptanceStatus(acceptanceStatus);
			userSubmission.setSubmittedCode(request.getSubmittedCode());
			userSubmission.setExecutionJobId(request.getExecutionJobId());

			if (acceptanceStatus.equals(AcceptanceStatus.ACCEPTED)) {
				JSONObject metadata = new JSONObject();
				metadata.put("processedTestCases", result.get("processedTestCases"));
				metadata.put("totalTestCases", result.get("totalTestCases"));
				userSubmission.setExecutionTime((Long) result.get("executionTime"));
				userSubmission.setExecutionMemory((Long) result.get("executionMemory"));

				userSubmission.setMetadata(metadata.toString());
			} else {
				JSONObject metadata = new JSONObject();
				if (acceptanceStatus.equals(AcceptanceStatus.COMPILATION_ERROR)) {
					metadata.put("compilationOutput", result.get("compilationOutput"));
				} else {
					String runTimeOutput = result.get("runtimeOutput").toString();
					if (StringUtils.isNotEmpty(runTimeOutput)) {
						metadata.put("runtimeOutput", runTimeOutput);
					}
					if (acceptanceStatus.equals(AcceptanceStatus.WRONG_ANSWER)) {
						metadata.put("output", result.get("output"));
					}
					metadata.put("input", result.get("input"));
					metadata.put("expected", result.get("expected"));
					metadata.put("processedTestCases", result.get("processedTestCases"));
					metadata.put("totalTestCases", result.get("totalTestCases"));
				}

				userSubmission.setExecutionTime(null);
				userSubmission.setExecutionMemory(null);
				userSubmission.setMetadata(metadata.toString());
			}

			long submissionId = new UserSubmissionsDAO(sqlSession).insert(userSubmission);

			job.setStatus(CodeExecutionJobStatus.COMPLETED.getStatus());
			job.setMetadata(null);
			job.setSubmissionId(submissionId);
			codeExecutionJobsDAO.update(job);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
			throw e;
		}
	}

}
