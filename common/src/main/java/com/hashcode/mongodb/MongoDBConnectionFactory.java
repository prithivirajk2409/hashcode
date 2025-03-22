//package com.hashcode.mongodb;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//
//import com.hashcode.utils.Constants;
//import com.mongodb.ConnectionString;
//import com.mongodb.MongoClientSettings;
//import com.mongodb.ServerApi;
//import com.mongodb.ServerApiVersion;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoClients;
//
//public class MongoDBConnectionFactory {
//
//	private static final Logger logger = LogManager.getLogger(MongoDBConnectionFactory.class);
//	private static MongoClient mongoClient;
//
//	static {
//		try {
//			ServerApi serverApi = ServerApi.builder().version(ServerApiVersion.V1).build();
//			MongoClientSettings clientSettings = MongoClientSettings.builder()
//					.applyConnectionString(new ConnectionString(Constants.KEY_MONG_DB_CONNECTION_STRING))
//					.serverApi(serverApi).applyToConnectionPoolSettings(builder -> builder.maxSize(100).minSize(10))
//					.build();
//
//			mongoClient = MongoClients.create(clientSettings);
//			logger.info("Mongo Client Initialized");
//		} catch (Exception e) {
//			logger.error(e);
//		}
//	}
//
//	public static void init() {
//
//	}
//
//	public static MongoClient getMongoClient() {
//		return mongoClient;
//	}
//
//	public static void setMongoClient(MongoClient mongoClient) {
//		MongoDBConnectionFactory.mongoClient = mongoClient;
//	}
//
//}