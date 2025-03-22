package com.hashcode.api.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.hashcode.api.aws.AwsSqsService;
import com.hashcode.api.config.ThreadContextVariables;
import com.hashcode.dao.CodeExecutionJobsDAO;
import com.hashcode.dao.CompanyTagMasterDAO;
import com.hashcode.dao.ProblemsDAO;
import com.hashcode.dao.TopicTagMasterDAO;
import com.hashcode.dao.UserSubmissionsDAO;
import com.hashcode.entity.CodeExecutionRequest;
import com.hashcode.entity.HCCodeExecutionJobs;
import com.hashcode.entity.HCCompanyTagMaster;
import com.hashcode.entity.HCProblems;
import com.hashcode.entity.HCTopicTagMaster;
import com.hashcode.entity.HCUsers;
import com.hashcode.entity.HCUserSubmissions;
import com.hashcode.enums.AcceptanceStatus;
import com.hashcode.enums.DatabaseProviderIdentifier;
import com.hashcode.enums.ExecutionType;
import com.hashcode.enums.ProgrammingLanguage;
import com.hashcode.api.response.ApiResponse;
import com.hashcode.api.service.ProblemService;
import com.hashcode.utils.CommonUtilities;
import com.hashcode.utils.Constants;
import com.hashcode.mybatis.DatabaseUtility;
import com.hashcode.api.utils.ResponseUtility;
import com.hashcode.codeconsumer.CodeConsumerMessage;
import com.hashcode.codeconsumer.CodeConsumerTaskType;

@RestController
@RequestMapping("/problem")
public class ProblemController {

	private static final Logger logger = LogManager.getLogger(ProblemController.class);

	@Autowired
	private ProblemService problemService;

	/*
	 * Api to fetch list of problems for current page page size = 50 param
	 * pageNumber
	 *
	 */
	@GetMapping(value = "/list/{pageNumber}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ApiResponse<Map<String, Object>>> getPaginatedProblems(@PathVariable Integer pageNumber) {
		Map<String, Object> data = new HashMap<>();
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			HCUsers user = ThreadContextVariables.getUser();
			int userId = user.getUserId();
			Integer totalProblemsCount = new ProblemsDAO(sqlSession).getTotalProblemsCount();
			List<HCProblems> problemsList = new ProblemsDAO(sqlSession).getProblemsByPageOffset(
					(pageNumber - 1) * Constants.DEFAULT_PAGE_SIZE, Constants.DEFAULT_PAGE_SIZE);

			List<Map<String, Object>> problemDataList = new ArrayList<>();
			for (HCProblems problem : problemsList) {
				int problemId = problem.getProblemId();
				Map<String, Object> problemData = new HashMap<>();
//				List<HCCompanyTagMaster> companyTagList = new CompanyTagMasterDAO(sqlSession)
//						.getCompanyTagsForProblem(problemId);
//				List<HCTopicTagMaster> topicTagList = new TopicTagMasterDAO(sqlSession)
//						.getTopicTagsForProblem(problemId);

				double acceptanceRate = new UserSubmissionsDAO(sqlSession).getAcceptanceRateForProblem(problemId);
				String status = new UserSubmissionsDAO(sqlSession).getAcceptanceStatusForUserAndProblem(userId,
						problemId);

				problemData.put("problemId", problem.getProblemId());
				problemData.put("problemName", problem.getProblemName());
				problemData.put("slug", problem.getSlug());
				problemData.put("difficulty", problem.getDifficulty());
				problemData.put("acceptanceRate", acceptanceRate);
				problemData.put("status",
						status == null ? -1 : (status.equals(AcceptanceStatus.ACCEPTED.toString()) ? 1 : 0));
//				problemData.put("companyTagList", companyTagList);
//				problemData.put("topicTagList", topicTagList);

				problemDataList.add(problemData);
			}
			data.put("questions", problemDataList);
			data.put("pagesCount", (totalProblemsCount + Constants.DEFAULT_PAGE_SIZE) / Constants.DEFAULT_PAGE_SIZE);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}

		return ResponseUtility.successResponse(data);
	}

	@GetMapping(value = "/{slug}/info", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ApiResponse<Map<String, Object>>> getProblemDetails(@PathVariable String slug) {
		Map<String, Object> data = new HashMap<>();
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			HCProblems problemDetails = new ProblemsDAO(sqlSession).getProblemBySlug(slug);
			data.put("problem_details", problemDetails);

		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return ResponseUtility.successResponse(data);

	}

	/*
	 * Path param - problem Id payload typed_code, programming_language
	 * 
	 */
	@PostMapping(value = "/{problemId}/interpret", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ApiResponse<Map<String, Object>>> validateAgainstTest(@RequestBody Map<String, Object> body,
			@PathVariable Integer problemId) {
		return problemService.populateValidationRequest(body, problemId, "TEST");
	}

	@PostMapping(value = "/{problemId}/submit", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ResponseStatus(value = HttpStatus.OK)
	public ResponseEntity<ApiResponse<Map<String, Object>>> validateAgainstSubmit(@RequestBody Map<String, Object> body,
			@PathVariable Integer problemId) {
		return problemService.populateValidationRequest(body, problemId, "SUBMIT");
	}

}