package com.hashcode.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hashcode.entity.HCTopicTagMaster;
import com.hashcode.utils.Constants;

public class TopicTagMasterDAO {

	private static final Logger logger = LogManager.getLogger(TopicTagMasterDAO.class);
	private SqlSession sqlSession;

	public TopicTagMasterDAO(SqlSession sqlSession) throws Exception {
		if (sqlSession == null) {
			throw new Exception("Sql session should not be null");
		}
		this.sqlSession = sqlSession;
	}

	public List<HCTopicTagMaster> getTopicTagsForProblem(int problemId) {
		List<HCTopicTagMaster> topicTags = null;
		try {
			Map<String, Object> params = new HashMap<>();
			params.put("problemId", problemId);
			topicTags = sqlSession.selectList("com.hashcode.persistence.HCTopicTagMaster.getTopicTagsForProblem",
					params);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return topicTags;
	}
}