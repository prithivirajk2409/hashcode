package com.hashcode.api;

import java.net.URL;

import org.apache.ibatis.session.SqlSession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.hashcode.enums.AppMode;
import com.hashcode.enums.DatabaseProviderIdentifier;
import com.hashcode.mybatis.DatabaseUtility;
import com.hashcode.utils.Constants;

@SpringBootApplication
@ComponentScan(basePackages = { "com.hashcode" })
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class, HibernateJpaAutoConfiguration.class })
public class RunApiService {

	private static final Logger logger = LogManager.getLogger(RunApiService.class);

	public static void setLog4j2Config() {
		try {
			String loggerFileName = Constants.APP_MODE.equals(AppMode.PRODUCTION) ? Constants.FILENAME_LOG_PROD_PROPERTIES
					: Constants.FILENAME_LOG_NON_PROD_PROPERTIES;
			URL loggerPath = RunApiService.class.getClassLoader().getResource(loggerFileName);
			LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
			loggerContext.setConfigLocation(loggerPath.toURI());
			loggerContext.updateLoggers();
			logger.info("environment : {} is using {} logger configuration", Constants.APP_MODE, loggerFileName);
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
	}

	public static void main(String[] args) {
		setLog4j2Config();
		try (SqlSession sqlSession = DatabaseUtility
				.getSqlSession(DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName())) {
			configureShutdownhooks();
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		SpringApplication.run(RunApiService.class, args);

	}

	public static void configureShutdownhooks() {
		Runtime.getRuntime().addShutdownHook(new Thread(() -> performShutdownActivities()));
	}

	public static void performShutdownActivities() {
		DatabaseUtility.shutDownAllConnectionPools();
	}

}