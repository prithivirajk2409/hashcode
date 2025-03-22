package com.hashcode.utils;

import com.hashcode.enums.AppMode;

public class Constants {

	private Constants() {

	}

	public static Boolean HIKARI_ENABLED = false;
	private static final String KEY_DB_PASSWORD = "db.password";
	private static final String APP_STRING = System.getProperty("tomcat.runtime.environment.version");
	public static final AppMode APP_MODE = AppMode.valueOf(APP_STRING.toUpperCase());
	public static final String DATABASE_ACCESS_KEY = System.getProperty(KEY_DB_PASSWORD,
			System.getenv(KEY_DB_PASSWORD));

	public static final String FILENAME_LOG_NON_PROD_PROPERTIES = "log4j2-non-prod.properties";
	public static final String FILENAME_LOG_PROD_PROPERTIES = "log4j2-prod.properties";

	public static final String CPP_MACROS = "#include <bits/stdc++.h>\nusing namespace std;";
	
	public static final String PYTHON_MACROS = "from typing import List";
	
	public static final String JAVA_MACROS = "import java.util.*;";

	public static final String KEY_MONGO_DB_DATABASE_NAME = "beta-hashcode-db";
	public static final String KEY_MONG_DB_CONNECTION_STRING = "mongodb+srv://prithivirajankumaresan:Hashcode123@c01.isdno.mongodb.net/?retryWrites=true&w=majority&appName=c01";

	public static final int DEFAULT_PAGE_SIZE = 50;

	public static final String KEY_EXCEPTION = "Exception";

	public static final String AWS_REGION = "us-east-1";

	public static final String FRONT_END_LOCALHOST_URL = "http://localhost:3000/";
	public static final String FRONT_END_PRODUCTION_URL = "https://hashcode.fun/";

	public static final String FRONT_END_URL = APP_MODE.equals(AppMode.PRODUCTION) ? FRONT_END_PRODUCTION_URL
			: FRONT_END_LOCALHOST_URL;
	
	
	public static final String WORKING_DIR_PATH = APP_MODE.equals(AppMode.PRODUCTION) ? "/app/execution/info/"
			: "/Users/prithivirajankumaresan/";
}