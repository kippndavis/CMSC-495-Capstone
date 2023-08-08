package com.mygdx.game;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.Timer;

public class GameScreen extends ScreenAdapter {

    TriviaGame game = TriviaGame.INSTANCE;
    private Viewport viewport;
    private Stage stage;
    private Timer questionTimer;
    private QuestionCard question;
    private int currentQuestionIndex;
    private Music clockTick;
    private Music currentMusic;
    private boolean timerTaskMade = false;
    private boolean spawnConfetti;
    private ArrayList<Confetti> confettiList;
    private Random random;
    protected String category;
    private String[][] questionData;

    public GameScreen(TriviaGame game, String category, String[][] questionData) {
        this.game = game;
        this.category = category;
        this.questionData = questionData;
    }

    public Timer getTimer() {
        return questionTimer;
    }

    @Override
    public void show() {
        // Load our assets
        game.assetManager = new AssetManager();
        game.assetManager.load("graphics/box.png", Texture.class);
        game.assetManager.load("graphics/boxCorrect.png", Texture.class);
        game.assetManager.load("graphics/boxHighlight.png", Texture.class);
        game.assetManager.load("graphics/boxSelected.png", Texture.class);
        game.assetManager.load("graphics/boxWrong.png", Texture.class);
        game.assetManager.load("music/1000Question.mp3", Music.class);
        game.assetManager.load("music/2000Question.mp3", Music.class);
        game.assetManager.load("music/16000Question.mp3", Music.class);
        game.assetManager.load("music/64000Question.mp3", Music.class);
        game.assetManager.load("music/250000Question.mp3", Music.class);
        game.assetManager.load("music/millionQuestion.mp3", Music.class);
        game.assetManager.load("sounds/clockCountdownFadeIn.mp3", Music.class);
        game.assetManager.load("sounds/answerDelay.mp3", Sound.class);
        game.assetManager.load("sounds/answerCorrect.mp3", Sound.class);
        game.assetManager.load("sounds/answerWrong.mp3", Sound.class);
        game.assetManager.load("sounds/gameWon.mp3", Sound.class);
        game.assetManager.finishLoading(); // Blocks until assets are done loading

        // Confetti variables
        confettiList = new ArrayList<>();
        spawnConfetti = false;
        random = new Random();
        
        // Define sounds (and preload)
        clockTick = game.assetManager.get("sounds/clockCountdownFadeIn.mp3", Music.class);
        currentMusic = game.assetManager.get("music/1000Question.mp3", Music.class);
        
        // Create a new Stage with a FitViewport
		viewport = new FitViewport(800, 480);
        stage = new Stage(viewport);

        // Create timer & label
        LabelStyle labelStyle = new LabelStyle(game.copperplateFont, Color.WHITE);

        questionTimer = new Timer();
        Timer.Task task = new Timer.Task() {
            @Override
            public void run() {
                question.timeRemaining--;
                question.timerLabel.setText(question.timeRemaining);

                float progress;
                if (question.timeRemaining > 15) {
                    // Stay at white color
                    progress = 1f;
                } else {
                    progress = 0.1f + 0.9f * (question.timeRemaining / 15f); 
                }

                float r = 1f;
                float g = progress; // Decrease green with progress
                float b = progress; // Decrease blue with progress

                question.timerLabel.setColor(new Color(r, g, b, 1));
                if (question.timeRemaining <= 0) {
                    this.cancel(); // Stop timer
                }
            }
        };
        questionTimer.scheduleTask(task, 0, 1); // Execute every 1 second

        //create sprites from loaded assets
        SpriteDrawable[] sprites = {new SpriteDrawable(new Sprite(game.assetManager.get("graphics/box.png", Texture.class))),
            new SpriteDrawable(new Sprite(game.assetManager.get("graphics/boxHighlight.png", Texture.class))),
            new SpriteDrawable(new Sprite(game.assetManager.get("graphics/boxCorrect.png", Texture.class))),
            new SpriteDrawable(new Sprite(game.assetManager.get("graphics/boxWrong.png", Texture.class))),
            new SpriteDrawable(new Sprite(game.assetManager.get("graphics/boxSelected.png", Texture.class)))
        };

        //create and populate array of styles for question buttons
        TextButton.TextButtonStyle[] boxStyles = new TextButton.TextButtonStyle[5];
        for (int i = 0; i < boxStyles.length; i++) {
            boxStyles[i] = new TextButton.TextButtonStyle();
            boxStyles[i].font = game.itcFont;
            boxStyles[i].up = sprites[0];
        }
        
        //create question card
        question = new QuestionCard(sprites, boxStyles, questionData, labelStyle);
        Color mintyWhiteGreen = new Color(180f/255f, 255f/255f, 180f/255f, 1);
        question.scoreLabel.setColor(mintyWhiteGreen);

        stage.addActor(question.getQuestionTable());

        currentQuestionIndex = 0;

        updateQuestion(currentQuestionIndex);

        // To allow handling input events, set the stage as the input processor
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        manageGameState();
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.spriteBatch.begin();
        game.spriteBatch.draw(game.background, 0, 0, 1200, 800);
        
        // Update and draw confetti
            if (spawnConfetti) {
                for (int i = 0; i < 5; i++) {
                    // X & Y are based off the viewport
                    float x = random.nextFloat() * 1200;
                    float y = 800;
                    float xSpeed = random.nextFloat() * 2 - 1;
                    float ySpeed = (-random.nextFloat() - 0.25f) * 2;
                    confettiList.add(new Confetti(x, y, xSpeed, ySpeed));
                }

	            for (int i = 0; i < confettiList.size(); i++) {
	                Confetti confetti = confettiList.get(i);
	                confetti.update();
	                confetti.draw(game.spriteBatch);
	
	                // Remove confetti if it's no longer visible
	                if (confetti.getY() < 0) {
	                    confettiList.remove(i);
	                    i--;  // Decrement i to avoid skipping the next element
	                }
	            }
            }
        
        game.spriteBatch.end();
        stage.act(delta);
        stage.draw();
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Preserves aspect ratio upon resize
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
        stage.dispose();
        confettiList.clear();
    }

    private void manageGameState() {

        // Checks if the user selected the right answer and disables other buttons
        // if correct moves on to the next question
        // if wrong game over screen
        boolean[] choice = question.manageButtons();

        if (choice[0] == true && choice[1] == true) {

            clockTick.stop();
            currentMusic.stop();
            
            if (!timerTaskMade) {
                timerTaskMade = true;
                questionTimer.stop();
                game.assetManager.get("sounds/answerDelay.mp3", Sound.class).play(game.soundVolume);
            	if (currentQuestionIndex <= 18) { // Not last question
            		// Update score & play sound
	                Timer.schedule(new Timer.Task() {
	                    @Override
	                    public void run() {
                        	game.assetManager.get("sounds/answerCorrect.mp3", Sound.class).play(game.soundVolume);
	                        question.userScore += 100 + (question.timeRemaining * 3);
	                        question.scoreLabel.setText(question.userScore);
	                    }
	                }, 3f);
	                // Go to next question
	                Timer.schedule(new Timer.Task() {
	                    @Override
	                    public void run() {
	                        currentQuestionIndex++;
	                        updateQuestion(currentQuestionIndex);
	                        timerTaskMade = false;
	                    }
	                }, 7f);
            	} else { // Last question
            		// Update score (with bonus) and allow confetti
            		Timer.schedule(new Timer.Task() {
	                    @Override
	                    public void run() {
                        	game.assetManager.get("sounds/gameWon.mp3", Sound.class).play(game.soundVolume);
	                        question.userScore += 300 + (question.timeRemaining * 3);
	                        question.scoreLabel.setText(question.userScore);
	                        spawnConfetti = true;
	                    }
	                }, 3f);
            		// Go to finish screen
            		Timer.schedule(new Timer.Task() {
	                    @Override
	                    public void run() {
	                    	game.setScreen(new FinishScreen(game, "Congratulations! You won!", category, question.userScore));
	                    	timerTaskMade = false;
	                    }
	                }, 22f);
            	}
            }

        } else if (choice[0] == true && choice[1] == false) {
        	
            clockTick.stop();
            currentMusic.stop();
        	
            if (!timerTaskMade) {
                timerTaskMade = true;
                questionTimer.stop();
                game.assetManager.get("sounds/answerDelay.mp3", Sound.class).play(game.soundVolume);
                // Play sound
        		Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        game.assetManager.get("sounds/answerWrong.mp3", Sound.class).play(game.soundVolume);
                    }
                }, 3f);
        		// Go to finish screen
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        game.setScreen(new FinishScreen(game, "You answered incorrectly!", category, question.userScore));
                        timerTaskMade = false;
                    }
                }, 7f);
            }

        } else if (question.timeRemaining <= 0) {
            questionTimer.stop();
            currentMusic.stop();
            game.setScreen(new FinishScreen(game, "You ran out of time!", category, question.userScore));
        }
    }

    private void updateQuestion(int questionIndex) {

        resetQuestion();
        questionTimer.start();
        
        // Music escalates with higher index questions
        if (questionIndex <= 3) {
        	currentMusic = game.assetManager.get("music/1000Question.mp3", Music.class);
        } else if (questionIndex <= 7) {
        	currentMusic = game.assetManager.get("music/2000Question.mp3", Music.class);
        } else if (questionIndex <= 11) {
        	currentMusic = game.assetManager.get("music/16000Question.mp3", Music.class);
        } else if (questionIndex <= 15) {
        	currentMusic = game.assetManager.get("music/64000Question.mp3", Music.class);
        } else if (questionIndex <= 18) {
        	currentMusic = game.assetManager.get("music/250000Question.mp3", Music.class);
        } else {
        	currentMusic = game.assetManager.get("music/millionQuestion.mp3", Music.class);
        }
        
        if (game.soundVolume > 0) { clockTick.play(); }
        clockTick.setVolume(game.soundVolume + 0.15f);
        currentMusic.play();
        currentMusic.setVolume(game.soundVolume);
        

        //first index in each nested array is question
        question.questionBox.setText(question.questionData[questionIndex][0]);

        //indices 1-4 are the answers
        int j = 1;
        for (int i = 0; i < question.answerBoxes.length; i++) {
            question.answerBoxes[i].setText(question.questionData[questionIndex][j]);
            j++;
        }

        //index 5 is temporarily a string that represents the index of the correct answer
        int correct = Integer.parseInt(question.questionData[questionIndex][5]);

        question.answerBoxes[correct].isCorrectAnswer = true;
    }

    //resets question card parameters to default values
    private void resetQuestion() {
        question.timeRemaining = 30; //set to whatever value you want the timer to be
        question.questionBox.setText("");

        for (int i = 0; i < question.answerBoxes.length; i++) {
            question.answerBoxes[i].setText("");
            question.answerBoxes[i].isCorrectAnswer = false;
            question.answerBoxes[i].isClicked = false;
            question.answerBoxes[i].isActive = true;
            question.answerBoxes[i].getStyle().up = question.answerBoxes[i].sprites[0];
        }
    }

}
