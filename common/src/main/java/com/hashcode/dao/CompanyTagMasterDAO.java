package com.hashcode.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hashcode.entity.HCCompanyTagMaster;
import com.hashcode.utils.Constants;

public class CompanyTagMasterDAO {

	private static final Logger logger = LogManager.getLogger(CompanyTagMasterDAO.class);
	private SqlSession sqlSession;

	public CompanyTagMasterDAO(SqlSession sqlSession) throws Exception {
		if (sqlSession == null) {
			throw new Exception("Sql session should not be null");
		}
		this.sqlSession = sqlSession;
	}

	public List<HCCompanyTagMaster> getCompanyTagsForProblem(int problemId) {
		List<HCCompanyTagMaster> companyTags = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("problemId", problemId);
			companyTags = sqlSession.selectList("com.hashcode.persistence.HCCompanyTagMaster.getCompanyTagsForProblem",
					params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return companyTags;
	}
}