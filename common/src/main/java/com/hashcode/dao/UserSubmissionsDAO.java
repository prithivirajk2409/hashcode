package com.hashcode.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hashcode.entity.HCUserSubmissions;
import com.hashcode.enums.AcceptanceStatus;
import com.hashcode.utils.Constants;

public class UserSubmissionsDAO {

	private static final Logger logger = LogManager.getLogger(UserSubmissionsDAO.class);

	private SqlSession sqlSession;

	public UserSubmissionsDAO(SqlSession sqlSession) throws Exception {
		if (sqlSession == null) {
			throw new Exception("Sql Session should not be null");
		}
		this.sqlSession = sqlSession;
	}

	public long insert(HCUserSubmissions userSubmission) {
		try {
			sqlSession.insert("com.hashcode.persistence.HCUserSubmissions.insert", userSubmission);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return userSubmission.getSubmissionId();
	}

	public HCUserSubmissions getUserSubmissionBySubmissionId(long submissionId) {
		HCUserSubmissions userSubmission = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("submissionId", submissionId);
			userSubmission = sqlSession
					.selectOne("com.hashcode.persistence.HCUserSubmissions.getUserSubmissionBySubmissionId", params);
			logger.info(userSubmission.toString());
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return userSubmission;
	}

	public List<HCUserSubmissions> getUserSubmissionsForUserAndProblem(int userId, int problemId) {
		List<HCUserSubmissions> userSubmissions = null;
		logger.info("{} {}", userId, problemId);
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("userId", userId);
			params.put("problemId", problemId);
			userSubmissions = sqlSession.selectList(
					"com.hashcode.persistence.HCUserSubmissions.getUserSubmissionsForUserAndProblem", params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return userSubmissions;
	}

	public Double getAcceptanceRateForProblem(int problemId) {
		Double acceptedRate = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("problemId", problemId);
			params.put("acceptanceStatus", AcceptanceStatus.ACCEPTED);
			acceptedRate = sqlSession
					.selectOne("com.hashcode.persistence.HCUserSubmissions.getAcceptanceRateForProblem", params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return acceptedRate;
	}

	public String getAcceptanceStatusForUserAndProblem(int userId, int problemId) {
		String status = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("problemId", problemId);
			params.put("userId", userId);
			status = sqlSession.selectOne(
					"com.hashcode.persistence.HCUserSubmissions.getAcceptanceStatusForUserAndProblem", params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return status;
	}
}