package com.hashcode.mybatis;

import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hashcode.utils.Constants;
import com.zaxxer.hikari.HikariDataSource;

public class DatabaseUtility {
	private static final Logger logger = LogManager.getLogger(DatabaseUtility.class);

	private DatabaseUtility() {

	}

	public SqlSession getSqlSession() {
		return getSqlSession(null);
	}

	public static SqlSession getSqlSession(String type) {
		try {
			SqlSession sqlSession = getSqlSessionFactory(type).openSession(true);
			return sqlSession;
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return null;
	}

	public static SqlSessionFactory getSqlSessionFactory(String type) throws Exception {
		return SqlSessionFactoryUtility.getSqlSessionFactory(type);
	}

	public static void shutDownAllConnectionPools() {
		for (Map.Entry<String, SqlSessionProvider> sqlSessionProvider : SqlSessionFactoryUtility
				.getSqlSessionProviders().entrySet()) {
			String poolType = sqlSessionProvider.getKey();
			logger.info("Shutting down pool : {}", poolType);
			shutDownConnectionPool(poolType);
			logger.info("Shutdown complete for pool : {}", poolType);
		}
	}

	public static void shutDownConnectionPool(String type) {
		DataSource dataSource;
		try {
			dataSource = getDataSource(type);
			if (dataSource instanceof PooledDataSource) {
				((PooledDataSource) dataSource).forceCloseAll();
			} else if (dataSource instanceof HikariDataSource) {
				((HikariDataSource) dataSource).close();
			}
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}

	public static DataSource getDataSource(String type) {
		return DatabaseUtility.getSqlSession(type).getConfiguration().getEnvironment().getDataSource();
	}
}