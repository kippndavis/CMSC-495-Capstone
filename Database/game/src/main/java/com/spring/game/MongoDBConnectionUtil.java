package com.spring.game;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import com.mongodb.client.MongoDatabase;

public class MongoDBConnectionUtil {
    private static final String DATABASE_NAME = "TriviaGPT";

    public static MongoCollection<Document> getCollection(String collectionName) {
        // MongoDB connection settings
        String connectionString = System.getenv("MONGODB_CONNECTION_STRING");
        
        // Create the MongoDB client
        MongoClient mongoClient = MongoClients.create(connectionString);

        // Access the MongoDB database and collection
        MongoDatabase database = mongoClient.getDatabase(DATABASE_NAME);
        return database.getCollection(collectionName);
    }
}
