package com.hashcode.dao;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONObject;

import com.hashcode.entity.HCTestCases;
import com.hashcode.utils.Constants;

public class TestCasesDAO {

	private static final Logger logger = LogManager.getLogger(TestCasesDAO.class);

	private SqlSession sqlSession;

	public TestCasesDAO(SqlSession sqlSession) throws Exception {
		if (sqlSession == null) {
			throw new Exception("Sql Session should not be null");
		}
		this.sqlSession = sqlSession;
	}
//	private static final String collectionName = "hctestcases";
//
//	public static String getTestCasesForProblemId(int problemId) {
//		String testCase = null;
//		try {
//			Map<String, Object> params = new HashMap<>();
//			params.put("problem_id", problemId);
//			Document document = MongoDBUtility.getDocument(collectionName, params);
//			testCase = new JSONObject(document.toJson()).getJSONArray("testcase").toString();
//		} catch (Exception e) {
//			logger.error(e);
//		}
//		return testCase;
//
//	}

	public HCTestCases getTestCasesForProblemId(int problemId) {
		HCTestCases testCase = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("problemId", problemId);
			testCase = sqlSession.selectOne("com.hashcode.persistence.HCTestCases.getTestCasesForProblemId", params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return testCase;
	}
}