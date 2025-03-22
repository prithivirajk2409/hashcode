//package com.hashcode.mongodb;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//import org.bson.Document;
//import org.bson.conversions.Bson;
//
//import com.hashcode.utils.Constants;
//import com.mongodb.client.MongoClient;
//import com.mongodb.client.MongoCollection;
//import com.mongodb.client.MongoDatabase;
//import com.mongodb.client.model.Filters;
//
//public class MongoDBUtility {
//
//	public static void closeClient() {
//		if (MongoDBConnectionFactory.getMongoClient() != null) {
//			MongoDBConnectionFactory.getMongoClient().close();
//		}
//	}
//
//	public static Document getDocument(String collectionName, Map<String, Object> params) {
//		MongoClient mongoClient = MongoDBConnectionFactory.getMongoClient();
//		MongoDatabase database = mongoClient.getDatabase(Constants.KEY_MONGO_DB_DATABASE_NAME);
//		MongoCollection<Document> collection = database.getCollection(collectionName);
//
//		List<Bson> filterList = new ArrayList<>();
//		for (Map.Entry<String, Object> entry : params.entrySet()) {
//			filterList.add(Filters.eq(entry.getKey(), entry.getValue()));
//		}
//
//		Document document = collection.find(Filters.and(filterList)).first();
//		return document;
//	}
//}