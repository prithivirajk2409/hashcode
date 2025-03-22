package com.hashcode.utils;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtilities {
	private static Logger logger = LogManager.getLogger(CommonUtilities.class);

	public static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

	public static ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	public static Object getFromJson(JSONObject json, String[] path) {
		if (path == null) {
			return null;
		} else {
			try {
				JSONObject result = json;
				int i = 0;
				for (; i < path.length && result != null && result.has(path[i]); i++) {
					if (i == path.length - 1) {
						return result.get(path[i]);
					}
					result = result.optJSONObject(path[i]);
					if (!(result instanceof JSONObject)) {
						logger.info("Not a JSONObject. Cannot get json in path {}.", path[i]);
						return null;
					}
				}
				logger.debug("Key {} is not found in json.", path[i]);
			} catch (Exception e) {
				logger.warn(Constants.KEY_EXCEPTION, e);
			}
		}
		return null;
	}

	public static String[] getPathForDBConnectionPoolConfig(String dbIdentifier) {
		String env = Constants.APP_MODE.name();
		String path[] = Constants.HIKARI_ENABLED ? new String[] { "others", "hikari", dbIdentifier }
				: new String[] { "others", dbIdentifier };
		if (env.equalsIgnoreCase("production")) {
			path[0] = "production";
		}

		logger.info("Pool path in config json {}", Arrays.toString(path));
		return path;
	}

	public static JSONObject loadDBConnectionPoolConfig(String enabledService, String dbIdentifierName) {
		JSONObject configJson = null;
		try {
//				String filePath = "postgres/" + enabledService.toLowerCase() + ".json";

//				URL uri = ClassLoader.getSystemResource("postgres/" + enabledService.toLowerCase() + ".json");
//				logger.info(uri.toString());
//				InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(filePath);
//				byte[] binaryData = inputStream.readAllBytes();
////				byte[] binaryData = FileCopyUtils.copyToByteArray(inputStream);
//				logger.info(binaryData.toString());
//				String content = new String(binaryData, StandardCharsets.UTF_8);
//				configJson = (JSONObject) CommonUtilities.getFromJson(new JSONObject(content),
//						getPathForDBConnectionPoolConfig(dbIdentifierName));
//				logger.info(configJson.toString());

			InputStream inputStream = CommonUtilities.class.getClassLoader()
					.getResourceAsStream("postgres/config.json");
//				URL resource = CommonUtilities.class.getClassLoader()
//						.getResource("postgres/" + enabledService + ".json");
//				String content = new String(Files.readAllBytes(Paths.get(resource.getPath())));
			String content = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
//				String content = "{\"production\":{\"default\":{\"poolMaximumCheckoutTime\":\"60000\",\"poolMaximumIdleConnections\":\"2\",\"poolMaximumActiveConnections\":\"20\"},\"hikari\":{\"default\":{\"poolMaximumCheckoutTime\":\"60000\",\"minimumIdle\":\"2\",\"maxPoolSize\":\"20\"}}},\"others\":{\"default\":{\"poolMaximumCheckoutTime\":\"60000\",\"poolMaximumIdleConnections\":\"2\",\"poolMaximumActiveConnections\":\"20\"},\"hikari\":{\"default\":{\"poolMaximumCheckoutTime\":\"60000\",\"minimumIdle\":\"2\",\"maxPoolSize\":\"20\"}}}}";
			logger.info("Content : {}", content);
			configJson = (JSONObject) CommonUtilities.getFromJson(new JSONObject(content),
					getPathForDBConnectionPoolConfig(dbIdentifierName));
			logger.info(configJson.toString());
		} catch (Exception e) {
			logger.warn(Constants.KEY_EXCEPTION, e);
		}
		return configJson;
	}

}