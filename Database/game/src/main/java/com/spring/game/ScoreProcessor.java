package com.spring.game;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import org.bson.Document;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Component
public class ScoreProcessor {
    private final MongoCollection<Document> userScoresCollection;

    public ScoreProcessor() {
        // Access the MongoDB database and collection
        userScoresCollection = MongoDBConnectionUtil.getCollection("high_scores");
    }

    // New method to get scores for a category in the high_scores collection
    public String[][] getCategoryScoresHighToLow(String category) {
        List<Document> userHighScores = new ArrayList<>();

        // Create a filter to match documents containing the specified field (category)
        Document filter = new Document(category, new Document("$exists", true));

        // Find the documents containing the specified field
        userScoresCollection.find(filter).into(userHighScores);

        // Create a list to store all scores
        List<ScoreEntry> allScores = new ArrayList<>();

        // Extract and store all scores from userHighScores list
        for (Document categoryDocument : userHighScores) {
            List<Document> categoryScores = categoryDocument.getList(category, Document.class);
            if (categoryScores != null && !categoryScores.isEmpty()) {
                for (Document scoreDocument : categoryScores) {
                    int score = scoreDocument.getInteger("score");
                    String initials = scoreDocument.getString("initials");
                    allScores.add(new ScoreEntry(score, initials));
                }
            }
        }

        // Sort allScores in descending order based on the "score" field
        allScores.sort(Comparator.comparingInt(ScoreEntry::getScore).reversed());

        // Convert the sorted scores to a String[][] array containing arrays of "score" and "initials"
        int numberOfResults = Math.min(allScores.size(), 25);
        String[][] results = new String[numberOfResults][2];
        for (int i = 0; i < numberOfResults; i++) {
            ScoreEntry scoreEntry = allScores.get(i);
            results[i][0] = String.valueOf(scoreEntry.getScore());
            results[i][1] = scoreEntry.getInitials();
        }

        return results;
    }

    // Custom class to represent a score entry
    private static class ScoreEntry {
        private final int score;
        private final String initials;

        public ScoreEntry(int score, String initials) {
            this.score = score;
            this.initials = initials;
        }

        public int getScore() {
            return score;
        }

        public String getInitials() {
            return initials;
        }
    }

//    // ********* DEPRECATED ********* //
//    // Method to get the highest scores of each user in rank order
//    public String[] getOverallScoresHighToLow() {
//        List<Object[]> userHighScores = new ArrayList<>();
//
//        // Define the projection to fetch only the username and high_score fields
//        Bson projection = Projections.fields(Projections.include("username", "high_score"));
//        BsonDocument projectionDocument = projection.toBsonDocument();
//
//        // Sort the results in descending order based on the high_score field
//        Bson sort = Sorts.descending("high_score");
//        BsonDocument sortDocument = sort.toBsonDocument();
//
//        // Get all documents from the collection with username and high_score fields only
//        try (MongoCursor<Document> cursor = userScoresCollection.find()
//                .projection(projectionDocument)
//                .sort(sortDocument)
//                .iterator()) {
//            while (cursor.hasNext()) {
//                Document document = cursor.next();
//                String username = document.getString("username");
//                int highScore = document.getInteger("high_score");
//                userHighScores.add(new Object[]{username, highScore});
//            }
//        }
//
//        // Convert List<Object[]> to String[]
//        String[] highScores = new String[userHighScores.size()];
//        for (int i = 0; i < userHighScores.size(); i++) {
//            Object[] userScore = userHighScores.get(i);
//            String username = (String) userScore[0];
//            int highScore = (int) userScore[1];
//            String userScoreString = "Username: " + username + ", High Score: " + highScore;
//            highScores[i] = userScoreString;
//        }
//
//        return highScores;
//    }

//    // ********* DEPRECATED ********* //
//    // Method to get rank order of scores for a given category
//    public String[] getCategoryScoresHighToLow(String category) {
//        List<String> resultStrings = new ArrayList<>();
//
//        // Define the filter to match the documents containing the desired category
//        Bson filter = Filters.eq("scores.category", category);
//
//        // Perform the aggregation with the specified filter and projection
//        List<? extends Bson> pipeline = List.of(
//                new Document("$match", filter),
//                new Document("$unwind", "$scores"),
//                new Document("$match", new Document("scores.category", category)),
//                new Document("$project", new Document("username", 1).append("score", "$scores.score")),
//                new Document("$sort", new Document("score", -1))
//        );
//
//        try (MongoCursor<Document> cursor = userScoresCollection.aggregate(pipeline).iterator()) {
//            while (cursor.hasNext()) {
//                Document document = cursor.next();
//                String username = document.getString("username");
//                int score = document.getInteger("score");
//                resultStrings.add("Username: " + username + ", Score: " + score);
//            }
//        }
//
//        // Convert the list to a String[]
//        String[] resultArray = new String[resultStrings.size()];
//        resultStrings.toArray(resultArray);
//        return resultArray;
//    }

    // Method to update user scores in the collection
    public void updateUserScore(String category, String initials, int score) {
        // Create a filter to match documents containing the specified field (category)
        Document filter = new Document(category, new Document("$exists", true));

        // Create the new entry to be added to the appropriate document
        Document newEntry = new Document("score", score)
                .append("initials", initials);

        // Update or insert the new entry to the appropriate document
        Document update = new Document("$push", new Document(category, newEntry));
        userScoresCollection.updateOne(filter, update, new UpdateOptions().upsert(true));
    }

//    // ********* DEPRECATED ********* //
//    // Method to add new user scores to the collection
//    private void addNewUserScore(String username, String category, int scoreForCategory) {
//        // Create a new document representing the user's data
//        Document newUserDocument = new Document("username", username)
//                .append("scores", List.of(
//                        new Document("category", "Mathematics").append("score", category.equals("Mathematics") ? scoreForCategory : 0),
//                        new Document("category", "History").append("score", category.equals("History") ? scoreForCategory : 0),
//                        new Document("category", "Entertainment").append("score", category.equals("Entertainment") ? scoreForCategory : 0),
//                        new Document("category", "Geography").append("score", category.equals("Geography") ? scoreForCategory : 0),
//                        new Document("category", "Sports").append("score", category.equals("Sports") ? scoreForCategory : 0),
//                        new Document("category", "Science-Tech").append("score", category.equals("Science-Tech") ? scoreForCategory : 0)
//                ))
//                .append("high_score", scoreForCategory);
//
//        // Insert the document into the collection
//        userScoresCollection.insertOne(newUserDocument);
//    }
}
