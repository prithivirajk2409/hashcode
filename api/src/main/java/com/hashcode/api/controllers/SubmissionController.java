package com.hashcode.api.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hashcode.api.config.ThreadContextVariables;
import com.hashcode.api.response.ApiResponse;
import com.hashcode.api.utils.ResponseUtility;
import com.hashcode.dao.CodeExecutionJobsDAO;
import com.hashcode.dao.ProblemsDAO;
import com.hashcode.dao.UserSubmissionsDAO;
import com.hashcode.entity.HCCodeExecutionJobs;
import com.hashcode.entity.HCProblems;
import com.hashcode.entity.HCUserSubmissions;
import com.hashcode.entity.HCUsers;
import com.hashcode.enums.AcceptanceStatus;
import com.hashcode.enums.CodeExecutionJobStatus;
import com.hashcode.enums.ExecutionType;
import com.hashcode.enums.DatabaseProviderIdentifier;
import com.hashcode.mybatis.DatabaseUtility;
import com.hashcode.utils.CommonUtilities;
import com.hashcode.utils.Constants;

@RestController
@RequestMapping("/submission")
public class SubmissionController {
	private static final Logger logger = LogManager.getLogger(SubmissionController.class);

	@GetMapping(value = "/{executionJobId}/check", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ApiResponse<Map<String, Object>>> checkExecutionJobStatus(
			@PathVariable Integer executionJobId) {
		Map<String, Object> data = new HashMap<>();
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			HCUsers user = ThreadContextVariables.getUser();
			int userId = user.getUserId();
			HCCodeExecutionJobs job = new CodeExecutionJobsDAO(sqlSession).getExecutionJobById(executionJobId);
			CodeExecutionJobStatus executionJobStatus = CodeExecutionJobStatus
					.getCodeExecutionJobStatus(job.getStatus());
			if (executionJobStatus.equals(CodeExecutionJobStatus.COMPLETED)) {
				if (job.getExecutionType().equals(ExecutionType.TEST)) {
					logger.info(job.getMetadata());
					Map<String, Object> testReport = CommonUtilities.getObjectMapper().readValue(job.getMetadata(),
							new TypeReference<Map<String, Object>>() {
							});
					data.put("testReport", testReport);
				} else {
					HCUserSubmissions userSubmission = new UserSubmissionsDAO(sqlSession)
							.getUserSubmissionBySubmissionId(job.getSubmissionId());
					List<HCUserSubmissions> submissionHistory = new UserSubmissionsDAO(sqlSession)
							.getUserSubmissionsForUserAndProblem(userId, userSubmission.getProblemId());
					data.put("userSubmission", userSubmission);
					data.put("submissionHistory", submissionHistory);
				}
			}
			data.put("status", executionJobStatus);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
			return ResponseUtility.errorResponse("Something went wrong");
		}
		return ResponseUtility.successResponse(data);
	}

	@GetMapping(value = "/{problemId}/list", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ApiResponse<Map<String, Object>>> getUserSubmissionsList(@PathVariable Integer problemId) {
		Map<String, Object> data = new HashMap<>();
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			HCUsers user = ThreadContextVariables.getUser();
			int userId = user.getUserId();
			List<HCUserSubmissions> userSubmissionsList = new UserSubmissionsDAO(sqlSession)
					.getUserSubmissionsForUserAndProblem(userId, problemId);
			data.put("userSubmissions", userSubmissionsList);

		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return ResponseUtility.successResponse(data);

	}

	@GetMapping(value = "/{submissionId}/info", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ApiResponse<Map<String, Object>>> getSubmissionDetails(@PathVariable Long submissionId) {
		Map<String, Object> data = new HashMap<>();
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			Thread.sleep(3000);
			HCUserSubmissions userSubmissionDetails = new UserSubmissionsDAO(sqlSession)
					.getUserSubmissionBySubmissionId(submissionId);
			HCProblems problemDetails = new ProblemsDAO(sqlSession)
					.getProblemById(userSubmissionDetails.getProblemId());
			data.put("submission_details", userSubmissionDetails);
			data.put("problem_name", problemDetails.getProblemName());
			data.put("slug", problemDetails.getSlug());

		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return ResponseUtility.successResponse(data);
	}
}