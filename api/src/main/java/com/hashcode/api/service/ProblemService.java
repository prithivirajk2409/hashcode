package com.hashcode.api.service;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.hashcode.api.aws.AwsSqsService;
import com.hashcode.api.config.ThreadContextVariables;
import com.hashcode.api.response.ApiResponse;
import com.hashcode.api.utils.ResponseUtility;
import com.hashcode.codeconsumer.CodeConsumerMessage;
import com.hashcode.codeconsumer.CodeConsumerTaskType;
import com.hashcode.dao.CodeExecutionJobsDAO;
import com.hashcode.entity.CodeExecutionRequest;
import com.hashcode.entity.HCCodeExecutionJobs;
import com.hashcode.entity.HCUsers;
import com.hashcode.enums.CodeExecutionJobStatus;
import com.hashcode.enums.DatabaseProviderIdentifier;
import com.hashcode.enums.ExecutionType;
import com.hashcode.enums.ProgrammingLanguage;
import com.hashcode.mybatis.DatabaseUtility;
import com.hashcode.utils.CommonUtilities;
import com.hashcode.utils.Constants;

@Service
public class ProblemService {

	private static final Logger logger = LogManager.getLogger(ProblemService.class);

	@Autowired
	private AwsSqsService sqsService;

	public ResponseEntity<ApiResponse<Map<String, Object>>> populateValidationRequest(Map<String, Object> body,
			Integer problemId, String type) {

		Map<String, Object> data = new HashMap<>();
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			HCUsers user = ThreadContextVariables.getUser();
			int userId = user.getUserId();
			String typedCode = body.get("typed_code").toString();
			ProgrammingLanguage programmingLanguage = ProgrammingLanguage
					.valueOf(body.get("programming_language").toString());

			long executionJobId = createAndInsertCodeExecutionJob(ExecutionType.valueOf(type));
			if (executionJobId == -1) {
				return ResponseUtility.errorResponse("Something went wrong");
			}
			CodeExecutionRequest request = new CodeExecutionRequest();
			request.setUserId(userId);
			request.setProblemId(problemId);
			request.setProgrammingLanguage(programmingLanguage);
			request.setSubmittedCode(typedCode);
			request.setExecutionJobId(executionJobId);

			CodeConsumerMessage message = new CodeConsumerMessage(MDC.get("tracer"), CodeConsumerTaskType.valueOf(type),
					CommonUtilities.getObjectMapper().writeValueAsString(request));
			logger.info(MDC.get("tracer"));

			sqsService.sendSqsMessage(message);

			data.put("jobId", executionJobId);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return ResponseUtility.successResponse(data);
	}

	public long createAndInsertCodeExecutionJob(ExecutionType type) {
		long jobExecutionId = -1;
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			HCCodeExecutionJobs job = new HCCodeExecutionJobs();
			job.setStatus(CodeExecutionJobStatus.PENDING.getStatus());
			job.setExecutionType(type);
			jobExecutionId = new CodeExecutionJobsDAO(sqlSession).insert(job);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return jobExecutionId;
	}
}