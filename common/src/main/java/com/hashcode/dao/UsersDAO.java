package com.hashcode.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hashcode.entity.HCUsers;
import com.hashcode.utils.Constants;

public class UsersDAO {
	private static final Logger logger = LogManager.getLogger(UsersDAO.class);

	private SqlSession sqlSession;

	public UsersDAO(SqlSession sqlSession) throws Exception {
		if (sqlSession == null) {
			throw new Exception("sql session should not be null");
		}
		this.sqlSession = sqlSession;
	}

	public void upsert(HCUsers user) {
		try {
			sqlSession.insert("com.hashcode.persistence.HCUsers.upsert", user);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}

	public HCUsers getUserByUserName(String userName) {
		Map<String, Object> params = new HashMap<>();
		try {
			params.put("userName", userName);
			return sqlSession.selectOne("com.hashcode.persistence.HCUsers.getUserByUserName", params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return null;
	}
	
	public HCUsers getUserByEmailId(String emailId) {
		Map<String, Object> params = new HashMap<>();
		try {
			params.put("emailId", emailId);
			return sqlSession.selectOne("com.hashcode.persistence.HCUsers.getUserByEmailId", params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return null;
	}

}