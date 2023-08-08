package com.spring.game;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.json.JSONArray;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.ArrayList;
import java.util.Collections;
//import java.util.concurrent.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.ExecutionException;
import org.json.JSONObject;


@Component
public class QuestionProcessor {
    private final MongoCollection<Document> questionCollection;
    private static final String API_KEY = System.getenv("OPENAI_API_KEY");    // needs to be set as an env
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String MODEL = "gpt-3.5-turbo-16k";

    public QuestionProcessor() {
        // Access the MongoDB database and collection
        questionCollection = MongoDBConnectionUtil.getCollection("trivia_questions");
    }

    public String[][] getRandomQuestionsByCategory(String category) {

        // Query the collection for the document of the given category
        Document query = new Document("module.name", category);
        Document result = questionCollection.find(query).first();

        // Retrieve the questions array from the document
        List<Document> questionDocuments = Objects.requireNonNull(result).getList("questions", Document.class);

        // Create a set of indices
        Set<Integer> indices = new HashSet<>();
        for (int i = 0; i < questionDocuments.size(); i++) {
            indices.add(i);
        }

        // Retrieve 25 random indices
        List<Integer> randomIndices = new ArrayList<>(indices);
        Collections.shuffle(randomIndices);
        randomIndices = randomIndices.subList(0, Math.min(25, randomIndices.size()));

        // Retrieve the questions at the random indices
        List<Document> randomQuestions = new ArrayList<>();
        for (int index : randomIndices) {
            randomQuestions.add(questionDocuments.get(index));
        }

        return convertDocuments(randomQuestions);
    }

    // Alternate method with different parameter signature
    public String[][] getRandomQuestionsByCategory(String category, int numberOfQuestions) {

        // Query the collection for the document of the given category
        Document query = new Document("module.name", category);
        Document result = questionCollection.find(query).first();

        // Retrieve the questions array from the document
        List<Document> questionDocuments = Objects.requireNonNull(result).getList("questions", Document.class);

        // Create a set of indices
        Set<Integer> indices = new HashSet<>();
        for (int i = 0; i < questionDocuments.size(); i++) {
            indices.add(i);
        }

        // Retrieve random indices
        List<Integer> randomIndices = new ArrayList<>(indices);
        Collections.shuffle(randomIndices);
        randomIndices = randomIndices.subList(0, Math.min(numberOfQuestions, randomIndices.size()));

        // Retrieve the questions at the random indices
        List<Document> randomQuestions = new ArrayList<>();
        for (int index : randomIndices) {
            randomQuestions.add(questionDocuments.get(index));
        }

        return convertDocuments(randomQuestions);
    }

    public String[][] convertDocuments(List<Document> documents) {
        int numDocuments = documents.size();
        int numColumns = 6; // Assuming each document will have 6 elements in the formatted array

        String[][] formattedDocuments = new String[numDocuments][numColumns];

        for (int i = 0; i < numDocuments; i++) {
            Document document = documents.get(i);

            String question = String.valueOf(document.get("question"));
            List<String> answers = (List<String>) document.get("answers");
            String correctAnswer = String.valueOf(document.get("correct_answer"));

            formattedDocuments[i][0] = question;

            for (int j = 0; j < answers.size(); j++) {
                formattedDocuments[i][j + 1] = answers.get(j);
            }

            formattedDocuments[i][numColumns - 1] = correctAnswer;
        }

        return formattedDocuments;
    }

    public String generateMultipleChoiceQuestions(String category) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
            conn.setDoOutput(true);

            String prompt = category + "\n"; // Include the category in the prompt
            prompt += "Output 20 questions as a JSON array of questions, with each individual question having the property 'question' for the question prompt, 'answers' as an array for the 4 choices, and correct_answer as the index of the correct choice in the answers array.";
            JSONObject messages = new JSONObject();
            messages.put("role", "user");
            messages.put("content", prompt);
            JSONArray messagesArray = new JSONArray();
            messagesArray.put(messages);

            // Set the desired number of questions and an approximate max_tokens value
            int numQuestions = 20;
            int approximateTokensPerQuestion = 175; // Adjust this value as needed
            int maxTokens = numQuestions * approximateTokensPerQuestion;

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("model", MODEL);
            jsonObject.put("messages", messagesArray);
            jsonObject.put("max_tokens", maxTokens);
            jsonObject.put("temperature", 0.7);

            String jsonInputString = jsonObject.toString();

            // Set a timeout value (in milliseconds) based on Heroku's limit
            int timeoutMillis = 29500;

            // Create an ExecutorService and submit the API call as a Callable
            ExecutorService executor = Executors.newSingleThreadExecutor();
            Future<String> future = executor.submit(() -> {
                try (OutputStream os = conn.getOutputStream()) {
                    byte[] input = jsonInputString.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int status = conn.getResponseCode();
                BufferedReader br = new BufferedReader(
                        new InputStreamReader(status == 200 ? conn.getInputStream() : conn.getErrorStream())
                );

                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line).append("\n");
                }
                br.close();

                conn.disconnect();
                return response.toString();
            });
            // Get the API response within the timeout
            String response = null;
            try {
                response = future.get(timeoutMillis, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                // Handle the timeout scenario here
                response = "AI API call timed out.";
                future.cancel(true); // Cancel the ongoing API request
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            // Shutdown the ExecutorService after getting the response
            executor.shutdown();

            conn.disconnect();
            return response;
        } catch (IOException e) {
            return "AI API call timed out.";
        }
    }
}
