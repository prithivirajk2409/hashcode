package com.hashcode.enums;

public enum DatabaseProviderIdentifier {

	PRIMARY("default", DBName.PRIMARY, DBUserType.USER);

	private final String dbIdentifierName;
	private final DBName dbName;
	private final DBUserType dbUserType;

	DatabaseProviderIdentifier(String dbIdentifierName, DBName dbName, DBUserType dbUserType) {
		this.dbIdentifierName = dbIdentifierName;
		this.dbName = dbName;
		this.dbUserType = dbUserType;
	}

	public String getDbIdentifierName() {
		return dbIdentifierName;
	}

	public DBName getDbName() {
		return dbName;
	}

	public DBUserType getDbUserType() {
		return dbUserType;
	}

	public static DatabaseProviderIdentifier getDatabaseProviderIdentifierByIdentifier(String dbIdentifierName) {
		for (DatabaseProviderIdentifier databaseProviderIdentifier : DatabaseProviderIdentifier.values()) {
			if (databaseProviderIdentifier.getDbIdentifierName().equalsIgnoreCase(dbIdentifierName))
				return databaseProviderIdentifier;
		}
		return null;
	}

}