package com.hashcode.mybatis;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.hashcode.enums.DatabaseProviderIdentifier;
import com.hashcode.utils.CommonUtilities;
import com.hashcode.utils.Constants;

@SessionProvider
public class DefaultSqlSessionProvider extends SqlSessionProvider {

	private static final Logger logger = LogManager.getLogger(DefaultSqlSessionProvider.class);
	private JSONObject dbConnectionPool = null;

	public DefaultSqlSessionProvider() {

	}

	@Override
	public String getIdentifier() {
		return DatabaseProviderIdentifier.PRIMARY.getDbIdentifierName();
	}

	@Override
	public String getFileName() {
		return "sql-maps-config.xml";
	}

	@Override
	public String getDatabasePasswordKey() {
		return Constants.DATABASE_ACCESS_KEY;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

	@Override
	public String description() {
		return "Main Db database connection";

	}

	@Override
	public JSONObject getDbConnectionPool() {
		String enabledServices = System.getProperty("services.enabled");
		if (dbConnectionPool == null) {
			this.dbConnectionPool = CommonUtilities.loadDBConnectionPoolConfig(enabledServices, getIdentifier());
		}
		return dbConnectionPool;
	}

}