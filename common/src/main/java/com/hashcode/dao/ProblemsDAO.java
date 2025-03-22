package com.hashcode.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hashcode.entity.HCProblems;
import com.hashcode.utils.Constants;

public class ProblemsDAO {

	private static final Logger logger = LogManager.getLogger(ProblemsDAO.class);

	private SqlSession sqlSession;

	public ProblemsDAO(SqlSession sqlSession) throws Exception {
		if (sqlSession == null) {
			throw new Exception("Sql Session can't be null");
		}
		this.sqlSession = sqlSession;
	}

	public Integer getTotalProblemsCount() {
		Integer count = 0;
		try {
			count = sqlSession.selectOne("com.hashcode.persistence.HCProblems.getTotalProblemsCount");
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return count;
	}

	public List<HCProblems> getProblemsByPageOffset(int offset, int pageSize) {
		List<HCProblems> problemsList = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("pageSize", pageSize);
			params.put("offset", offset);
			logger.info("{}, {}", pageSize, offset);
			problemsList = sqlSession.selectList("com.hashcode.persistence.HCProblems.getProblemsByPageOffset", params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return problemsList;
	}

	public HCProblems getProblemBySlug(String slug) {
		HCProblems problem = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("slug", slug);
			problem = sqlSession.selectOne("com.hashcode.persistence.HCProblems.getProblemBySlug", params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return problem;
	}

	public HCProblems getProblemById(int problemId) {
		HCProblems problem = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("problemId", problemId);
			problem = sqlSession.selectOne("com.hashcode.persistence.HCProblems.getProblemById", params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return problem;
	}
}