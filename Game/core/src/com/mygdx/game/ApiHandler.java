package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net.HttpMethods;
import com.badlogic.gdx.Net.HttpRequest;
import com.badlogic.gdx.Net.HttpResponse;
import com.badlogic.gdx.Net.HttpResponseListener;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApiHandler {
	
    private String requestType;

    private ApiResponseHandler responseHandler;
    private ApiFailureHandler failureHandler;

    public ApiHandler(ApiResponseHandler responseHandler, ApiFailureHandler failureHandler) {
        this.responseHandler = responseHandler;
        this.failureHandler = failureHandler;
    }

    //main method for requesting questions/scores from the database
    //ex: https://mkress-d8cb8becfffc.herokuapp.com/questions/random/History/10
    public void getRequest(String category, String requestType) {
    	
    	String apiUrl;
    	this.requestType = requestType;
    	switch (requestType) {
    		case "questions":
    	        apiUrl = "https://mkress-d8cb8becfffc.herokuapp.com/questions/random/" + category + "/20";
    	        break;
    		case "scores":
    	        apiUrl = "https://mkress-d8cb8becfffc.herokuapp.com/scores/high-scores/" + category;
    	        break;
    		default:
    			return;
    	}

        HttpRequest request = new HttpRequest(HttpMethods.GET);
        request.setUrl(apiUrl);

        MyListener listener = new MyListener();

        Gdx.net.sendHttpRequest(request, listener);
    }
    
    //main method for submitting scores to the database
    //ex: https://mkress-d8cb8becfffc.herokuapp.com/scores/submit-score/entertainment?initials=ASS&score=6969
    public void postRequest(String category, String initials, int userScore) {

    	category = category.toLowerCase();
    	if (category.equals("wildcard/gpt")) { category = "wildcard-gpt"; }
    	initials = initials.toUpperCase();
    	this.requestType = "";
        HttpRequest request = new HttpRequest(HttpMethods.POST);
        String apiUrl = "https://mkress-d8cb8becfffc.herokuapp.com/scores/submit-score/" + category + "?initials=" + initials + "&score=" + userScore;
        System.out.println(apiUrl);
        request.setUrl(apiUrl);

        MyListener listener = new MyListener();

        Gdx.net.sendHttpRequest(request, listener);
    }
    
    public void postRequest(String userInput){
        String apiUrl = "https://mkress-d8cb8becfffc.herokuapp.com/questions/gpt?category=" + userInput;
        
        HttpRequest request = new HttpRequest(HttpMethods.POST);
        request.setUrl(apiUrl);
        
        requestType = "AI";
        
        MyListener listener =  new MyListener();
        
        Gdx.net.sendHttpRequest(request, listener);
    }
    
    private String[][] formatQuestionsResponse(String response) {
        //trim brackets, break into individual tokens using double quotes, store elements into output array
        response = response.replace('[', ' ');
        response = response.replace(']', ' ');
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(response);

        String[][] formattedResponse = new String[25][6];

        //nested for loop but with boolean condition of finding matches
        int i = 0;
        int j = 0;
        while (m.find()) {
            formattedResponse[i][j] = m.group(1);
            j++;
            if (j >= 6) {
                j = 0;
                i++;
            }
            if (i >= 25) {
                break;
            }
        }
        
        return formattedResponse;
    }
    
    private String[][] formatScoresResponse(String response) {
        // Trim brackets, break into individual tokens using double quotes, store elements into output array
        response = response.replace('[', ' ');
        response = response.replace(']', ' ');
        Pattern p = Pattern.compile("\"([^\"]*)\"");
        Matcher m = p.matcher(response);

        String[][] formattedResponse = new String[25][2];

        // Nested for loop but with boolean condition of finding matches
        int i = 0;
        int j = 0;
        while (m.find()) {
            formattedResponse[i][j] = m.group(1);
            j++;
            if (j >= 2) {  // Note the change to 2 here
                j = 0;
                i++;
            }
            if (i >= 25) {
                break;
            }
        }
        return formattedResponse;
    }
    
    private String[][] formatAiQuestionsResponse(String response){

        //retrieve just questions information from the response
        JsonReader reader = new JsonReader();
        JsonValue apiResponse = reader.parse(response);
        //Gdx.app.log("original", apiResponse.toString());
        
        JsonValue choicesJson = apiResponse.get("choices");
        //Gdx.app.log("1", choicesJson.toString());
        JsonValue messageJson = choicesJson.get(0).get("message");
        //Gdx.app.log("2", messageJson.toString());
        
        String contentString = messageJson.getString("content");
        JsonValue contentJson = reader.parse(contentString);
        //Gdx.app.log("3", contentJson.toString());
        
        JsonValue questionsJson;
        if(contentString.contains("questions")){
            questionsJson = contentJson.get("questions");
            //Gdx.app.log("4", questionsJson.toString());    
        } else{
            questionsJson = contentJson;
        }
        
        String[][] formattedResponse = new String[questionsJson.size][6];
        
        //Gdx.app.log("#Qs", Integer.toString(questionsJson.size));
        
        //parsing the questions data into output array
        for(int questionIndex = 0; questionIndex < questionsJson.size; questionIndex++){
            
            JsonValue questionInfo = questionsJson.get(questionIndex);
            
            
            formattedResponse[questionIndex][0] = questionInfo.getString("question");
            //Gdx.app.log("Q"+(questionIndex+1), formattedResponse[questionIndex][0]);
            
            JsonValue answersJson = questionInfo.get("answers");
            for(int j = 0; j < answersJson.size; j++){
                formattedResponse[questionIndex][j+1] = answersJson.getString(j);
                //Gdx.app.log("A"+(j+1), formattedResponse[questionIndex][j+1]);
            }
            
            formattedResponse[questionIndex][5] = questionInfo.getString("correct_answer");
            //Gdx.app.log("CA", formattedResponse[questionIndex][5]);
        }
        
        return formattedResponse;
    }

    //helper class that defines the what happens when sending the request
    private class MyListener implements HttpResponseListener {

        String response;
        String[][] formattedResponse;

        @Override
        public void handleHttpResponse(HttpResponse httpResponse) {
            response = httpResponse.getResultAsString();
            if (requestType.equals("questions")) {
            	formattedResponse = formatQuestionsResponse(response);
            } else if (requestType.equals("scores")) {
            	formattedResponse = formatScoresResponse(response);
            } else if(requestType.equals("AI")){
                formattedResponse = formatAiQuestionsResponse(response);
            }
            
            //For dealing with asynchronous processes
            Gdx.app.postRunnable(new Runnable() {

                @Override
                public void run() {
                    responseHandler.handle(formattedResponse);
                }
            });
        }

        @Override
        public void failed(Throwable t) {
            //example, need specific error logic later
            failureHandler.failure(t);
            Gdx.app.log("API CALL FAILED", t.getMessage());
        }

        @Override
        public void cancelled() {
            //logic for when the request is cancelled
        }
    }

    //helps to deal with OpenGL asynchronous behavior when building out the question card
    public interface ApiResponseHandler {

        void handle(String[][] response);
    }
    
    public interface ApiFailureHandler{
        void failure(Throwable t);
    }
}