package com.hashcode.mybatis;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.hashcode.utils.Constants;

public class SqlSessionFactoryUtility {
	private static final Logger logger = LogManager.getLogger(SqlSessionFactoryUtility.class);
	private static Map<String, SqlSessionProvider> sqlSessionProviders = new HashMap<>();

	private SqlSessionFactoryUtility() {

	}

	static {
		try {
			populateSqlSessionProviders();
			initSqlSessionProviders();
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}

	public static void populateSqlSessionProviders() {
		Reflections reflections = new Reflections(new ConfigurationBuilder().setExpandSuperTypes(false)
				.filterInputsBy(new FilterBuilder().includePackage("com.hashcode")).forPackage("com.hashcode")
				.setScanners(Scanners.SubTypes, Scanners.TypesAnnotated));
		try {
			for (Class<?> providerClass : reflections.getTypesAnnotatedWith(SessionProvider.class)) {
				logger.info("Class Found : " + providerClass.getName());
				if (SqlSessionProvider.class.isAssignableFrom(providerClass)) {
					SqlSessionProvider sqlSessionProvider = (SqlSessionProvider) providerClass.getConstructor()
							.newInstance();
					sqlSessionProviders.put(sqlSessionProvider.getIdentifier(), sqlSessionProvider);
					logger.info("Session Provider Found : {}", sqlSessionProvider.toString());
				}
			}
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}

	public static void initSqlSessionProviders() throws IOException {
		String appMode = Constants.APP_MODE.toString().toLowerCase();
		for (SqlSessionProvider sqlSessionProvider : sqlSessionProviders.values()) {
			logger.info("Provider : {}", sqlSessionProvider.toString());
			Reader reader = Resources.getResourceAsReader(sqlSessionProvider.getFileName());
			Properties properties = new Properties();
			fillProperties(properties, sqlSessionProvider.getDbConnectionPool());
			SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader, appMode, properties);
			logger.info(sqlSessionFactory.toString());
			sqlSessionProvider.setSqlSessionFactory(sqlSessionFactory);
			DataSource dataSource = sqlSessionProvider.getSqlSessionFactory().getConfiguration().getEnvironment()
					.getDataSource();
			logger.info("Sessionfactory set for type :" + sqlSessionProvider.toString());
			if (dataSource instanceof PooledDataSource) {
				PooledDataSource pds = (PooledDataSource) dataSource;
				logger.info("DB Connection Pool Stats  for {} : {}", sqlSessionProvider.getIdentifier(),
						((PooledDataSource) dataSource).getPoolState());

				logger.info("JDBC URL: " + pds.getUrl());
				logger.info("Username: " + pds.getUsername());
				logger.info("Password: " + pds.getPassword());
			}
		}
	}

	public static void fillProperties(Properties properties, JSONObject dbConnectionPool) {
		properties.setProperty("poolMaximumCheckoutTime", dbConnectionPool.getString("poolMaximumCheckoutTime"));
		properties.setProperty("password", Constants.DATABASE_ACCESS_KEY);
		properties.setProperty("username", "postgres");

		if (Constants.HIKARI_ENABLED) {
			properties.setProperty("minimumIdle", dbConnectionPool.getString("minimumIdle"));
			properties.setProperty("maxPoolSize", dbConnectionPool.getString("maxPoolSize"));
		} else {
			properties.setProperty("poolMaximumIdleConnections",
					dbConnectionPool.getString("poolMaximumIdleConnections"));
			properties.setProperty("poolMaximumActiveConnections",
					dbConnectionPool.getString("poolMaximumActiveConnections"));
		}
	}

	public static SqlSessionFactory getSqlSessionFactory(String type) throws Exception {
		if (!sqlSessionProviders.containsKey(type)) {
			throw new Exception("Session factory not found with type : " + type);
		}
		return sqlSessionProviders.get(type).getSqlSessionFactory();

	}

	public static Map<String, SqlSessionProvider> getSqlSessionProviders() {
		return sqlSessionProviders;
	}

	public static void setSqlSessionProviders(Map<String, SqlSessionProvider> sqlSessionProviders) {
		SqlSessionFactoryUtility.sqlSessionProviders = sqlSessionProviders;
	}

}