package com.hashcode.mybatis;

import org.apache.ibatis.session.SqlSessionFactory;
import org.json.JSONObject;

public abstract class SqlSessionProvider {
	public SqlSessionFactory sqlSessionFactory;
	public JSONObject dbConnectionPool = null;

	public abstract String getIdentifier();

	public abstract String getFileName();

	public abstract String getDatabasePasswordKey();

	public abstract boolean isDefault();

	public abstract String description();

	public SqlSessionFactory getSqlSessionFactory() {
		return sqlSessionFactory;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public JSONObject getDbConnectionPool() {
		return dbConnectionPool;
	}

	public void setDbConnectionPool(JSONObject dbConnectionPool) {
		this.dbConnectionPool = dbConnectionPool;
	}

	@Override
	public String toString() {
		return "SqlSessionProvider [sqlSessionFactory=" + sqlSessionFactory + ", dbConnectionPool=" + dbConnectionPool
				+ ", getIdentifier()=" + getIdentifier() + ", getFileName()=" + getFileName()
				+ ", getDatabasePasswordKey()=" + getDatabasePasswordKey() + ", isDefault()=" + isDefault()
				+ ", description()=" + description() + ", getSqlSessionFactory()=" + getSqlSessionFactory()
				+ ", getDbConnectionPool()=" + getDbConnectionPool() + "]";
	}

}