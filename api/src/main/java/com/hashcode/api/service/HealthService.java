package com.hashcode.api.service;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.hashcode.api.response.SystemHealthResponse;
import com.hashcode.dao.InternalHealthDAO;
import com.hashcode.enums.DatabaseProviderIdentifier;
import com.hashcode.mybatis.DatabaseUtility;
import com.hashcode.utils.Constants;

@Service
public class HealthService {

	private static final Logger logger = LogManager.getLogger(HealthService.class);

	public SystemHealthResponse getHealthCheck() throws Exception {
		SystemHealthResponse systemHealthResponse = new SystemHealthResponse();
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			logger.info("Fetching from default xml :: {}", new InternalHealthDAO(sqlSession).getOne());
			systemHealthResponse.setDefaultDatabaseHealth(true);
			systemHealthResponse.updateHealthy();
			logger.info("Response Healthcheck at :: {} is :: {}", System.currentTimeMillis(), systemHealthResponse);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return systemHealthResponse;
	}

}