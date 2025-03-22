package com.hashcode.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProblemCompanyTagMapperDAO {

	private static final Logger logger = LogManager.getLogger(ProblemCompanyTagMapperDAO.class);

	private SqlSession sqlSession;

	public ProblemCompanyTagMapperDAO(SqlSession sqlSession) throws Exception {
		if (sqlSession == null) {
			throw new Exception("sql session should not be null");
		}
		this.sqlSession = sqlSession;
	}
}