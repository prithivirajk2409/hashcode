package com.hashcode.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hashcode.entity.HCCodeExecutionJobs;
import com.hashcode.utils.Constants;

public class CodeExecutionJobsDAO {

	private static final Logger logger = LogManager.getLogger(CodeExecutionJobsDAO.class);

	private SqlSession sqlSession;

	public CodeExecutionJobsDAO(SqlSession sqlSession) throws Exception {
		if (sqlSession == null) {
			throw new Exception("Sql session should not be null");
		}
		this.sqlSession = sqlSession;
	}

	public long insert(HCCodeExecutionJobs job) {
		try {
			sqlSession.insert("com.hashcode.persistence.HCCodeExecutionJobs.insert", job);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return job.getExecutionJobId();
	}

	public void update(HCCodeExecutionJobs job) {
		try {
			sqlSession.update("com.hashcode.persistence.HCCodeExecutionJobs.update", job);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}

	public HCCodeExecutionJobs getExecutionJobById(long executionJobId) {
		HCCodeExecutionJobs job = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("executionJobId", executionJobId);
			job = sqlSession.selectOne("com.hashcode.persistence.HCCodeExecutionJobs.getExecutionJobById", params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return job;
	}

}