package com.hashcode.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hashcode.utils.Constants;

public class InternalHealthDAO {
	private static final Logger logger = LogManager.getLogger(InternalHealthDAO.class);

	private SqlSession sqlSession;

	public InternalHealthDAO(SqlSession sqlSession) {
		if (sqlSession == null) {
			throw new IllegalArgumentException("sqlSession should not be null");

		}
		this.sqlSession = sqlSession;
	}

	public int getOne() {
		int one = 0;
		try {
			one = sqlSession.selectOne("com.hashcode.persistence.HCInternalHealth.getOne");
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return one;
	}
}