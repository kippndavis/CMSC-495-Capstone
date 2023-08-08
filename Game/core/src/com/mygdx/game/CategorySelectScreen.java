package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class CategorySelectScreen extends ScreenAdapter {

    TriviaGame game = TriviaGame.INSTANCE;
    Viewport viewport;
    Stage stage;
    Stage spotlightStage;
    private Table mainTable;
    private Table inputTable;
    private TextField inputField;
    private Texture fadeTexture;
    private float alpha;
    private boolean transition;
    private Spotlight leftSpotlight;
    private Spotlight rightSpotlight;
    private Texture mockQuestion;
    
    private Color startColor = new Color(0.5f, 1, 0.5f, 1); // Light green
    private Color endColor = new Color(0, 0.5f, 0, 1); // Darker green
    private float t = 0;
    private Label statusMessageFlash;

    public CategorySelectScreen(TriviaGame game) {
        this.game = game;
    }

    // Helper methods to reduce duplication
    private Image createImage(String asset, float newScaleX, float newScaleY) {
        Texture texture = game.assetManager.get(asset, Texture.class);
        Image image = new Image(texture);
        float newOriginX = texture.getWidth() / 2 * newScaleX;
        float newOriginY = texture.getHeight() / 2 * newScaleY;
        image.setOrigin(newOriginX, newOriginY);
        image.setScale(newScaleX, newScaleY);
        return image;
    }
    
    @Override
    public void show() {

        // Load the assets required for this screen
    	game.assetManager.load("graphics/ButtonUp.png", Texture.class);
        game.assetManager.load("graphics/ButtonDown.png", Texture.class);
        game.assetManager.load("graphics/chooseCategory.png", Texture.class);
        game.assetManager.load("graphics/entertainment.png", Texture.class);
        game.assetManager.load("graphics/geography.png", Texture.class);
        game.assetManager.load("graphics/history.png", Texture.class);
        game.assetManager.load("graphics/scienceTech.png", Texture.class);
        game.assetManager.load("graphics/sports.png", Texture.class);
        game.assetManager.load("graphics/gpt.png", Texture.class);
        game.assetManager.load("graphics/textFieldBackground.png", Texture.class);
        game.assetManager.load("graphics/textFieldFocusedBackground.png", Texture.class);
        game.assetManager.load("graphics/textFieldCursor.png", Texture.class);
        game.assetManager.load("graphics/textFieldSelection.png", Texture.class);
        game.assetManager.load("graphics/mockQuestion.png", Texture.class);
        game.assetManager.load("sounds/lightSwitch.mp3", Sound.class);
        game.assetManager.load("sounds/lightsToCentre.mp3", Sound.class);
        game.assetManager.load("sounds/buttonClick.mp3", Sound.class);
        game.assetManager.finishLoading(); // Blocks until assets are done loading

        viewport = new FitViewport(800, 480);
        stage = new Stage(viewport);
        spotlightStage = new Stage(viewport);
        
        // Create labels for wait message
        LabelStyle labelStyle = new LabelStyle(game.itcFont, Color.WHITE);
        final Label statusMessageStart = new Label("Your questions are ", labelStyle);
        statusMessageFlash = new Label("generating...", labelStyle);
        final Label statusMessageEnd = new Label("This may take a moment.", labelStyle);
        statusMessageStart.setVisible(false);
        statusMessageFlash.setVisible(false);
        statusMessageEnd.setVisible(false);

        // Define fonts and pictures, and styles for buttons
        Sprite buttonUpSprite = new Sprite(game.assetManager.get("graphics/ButtonUp.png", Texture.class));
        buttonUpSprite.setSize(150, 75);
        Sprite buttonDownSprite = new Sprite(game.assetManager.get("graphics/ButtonDown.png", Texture.class));
        buttonDownSprite.setSize(150, 75);
        TextButton.TextButtonStyle menuButtonStyle = new TextButton.TextButtonStyle();
        menuButtonStyle.font = game.itcFont;
        menuButtonStyle.up = new SpriteDrawable(buttonUpSprite);
        menuButtonStyle.down = new SpriteDrawable(buttonDownSprite);
        
        // Create the text field assets, style & object
        Texture textFieldBackgroundTexture = game.assetManager.get("graphics/textFieldBackground.png", Texture.class);
        Texture textFieldFocusedBackgroundTexture = game.assetManager.get("graphics/textFieldFocusedBackground.png", Texture.class);
        Texture textFieldCursorTexture = game.assetManager.get("graphics/textFieldCursor.png", Texture.class);
        Texture textFieldCursorSelectionTexture = game.assetManager.get("graphics/textFieldSelection.png", Texture.class);
        TextureRegionDrawable textFieldBackground = new TextureRegionDrawable(new TextureRegion(textFieldBackgroundTexture));
        TextureRegionDrawable textFieldFocusedBackground = new TextureRegionDrawable(new TextureRegion(textFieldFocusedBackgroundTexture));
        TextureRegionDrawable textFieldCursor = new TextureRegionDrawable(new TextureRegion(textFieldCursorTexture));
        TextureRegionDrawable textFieldSelection = new TextureRegionDrawable(new TextureRegion(textFieldCursorSelectionTexture));
        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = game.itcFont;
        textFieldStyle.fontColor = Color.WHITE; // replace this with actual color from the skin if needed
        textFieldStyle.focusedFontColor = Color.WHITE;
        textFieldStyle.background = textFieldBackground;
        textFieldStyle.focusedBackground = textFieldFocusedBackground;
        textFieldStyle.cursor = textFieldCursor;
        textFieldStyle.selection = textFieldSelection;
        inputField = new TextField("Enter category...", textFieldStyle);
        inputField.setAlignment(Align.center);

        // Create images
        Image chooseCategoryImage = createImage("graphics/chooseCategory.png", 0.8f, 1f);
        Image entertainmentImage = createImage("graphics/entertainment.png", 0.4f, 0.4f);
        Image geographyImage = createImage("graphics/geography.png", 0.4f, 0.4f);
        Image historyImage = createImage("graphics/history.png", 0.4f, 0.4f);
        Image scienceTechImage = createImage("graphics/scienceTech.png", 0.4f, 0.4f);
        Image sportsImage = createImage("graphics/sports.png", 0.4f, 0.4f);
        Image gptImage = createImage("graphics/gpt.png", 0.4f, 0.4f);
        
        // Get textures and variables for screen transition
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(0, 0, 0, 1); // Black color
        pixmap.fill();
        fadeTexture = new Texture(pixmap);
        alpha = 1; // Start fully opaque
        pixmap.dispose();
        transition = false; // Test, make false later
	    leftSpotlight = new Spotlight(-300, 100, -50, 800, 1450, 100, 0, 135, 50, 100);
	    rightSpotlight = new Spotlight(1500, 100, 1250, 800, -250, 100, 0, 135, 50, 100);
	    mockQuestion = game.assetManager.get("graphics/mockQuestion.png", Texture.class);

        // Create TextButtons with their respective styles
        final TextButton entertainmentButton = new TextButton("Entertainment", menuButtonStyle);
        final TextButton geographyButton = new TextButton("Geography", menuButtonStyle);
        final TextButton historyButton = new TextButton("History", menuButtonStyle);
        final TextButton scienceTechButton = new TextButton("Science & Tech", menuButtonStyle);
        final TextButton sportsButton = new TextButton("Sports", menuButtonStyle);
        final TextButton gptButton = new TextButton("You Decide!", menuButtonStyle);
        final TextButton inputSubmitButton = new TextButton("Submit", menuButtonStyle);
        final TextButton inputCancelButton = new TextButton("Cancel", menuButtonStyle);
        
        //for each action listener, unique api handlers are formed based on category
        entertainmentButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isOver()) {
                	game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
                    //Pull database questions for Entertainment
                    final ApiHandler apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler(){
                    
                        @Override
                        public void handle(String[][] response){
                            game.setScreen(new GameScreen(game, "Entertainment", response));
                        }
                        
                    },
                    new ApiHandler.ApiFailureHandler(){
                        
                        @Override
                        public void failure(Throwable t){
                            game.setScreen(new MainMenuScreen(game));
                        }
                    });
            	    transitionTriggers();
            	    Timer.schedule(new Timer.Task(){ @Override public void run() { apiHandler.getRequest("Entertainment", "questions"); } }, 5f);
                }
            }
        });

        geographyButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isOver()) {
                	game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
                    //Pull database questions for Geography
                    final ApiHandler apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler(){
                    
                        @Override
                        public void handle(String[][] response){
                            game.setScreen(new GameScreen(game, "Geography", response));
                        }
                        
                    },
                    new ApiHandler.ApiFailureHandler(){
                        
                        @Override
                        public void failure(Throwable t){
                            game.setScreen(new MainMenuScreen(game));
                        }
                    });
            	    transitionTriggers();
           	     	Timer.schedule(new Timer.Task(){ @Override public void run() { apiHandler.getRequest("Geography", "questions"); } }, 5f);
                }
            }
        });

        historyButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isOver()) {
                	game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
                    //Pull database questions for History
                    final ApiHandler apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler(){
                    
                        @Override
                        public void handle(String[][] response){
                            game.setScreen(new GameScreen(game, "History", response));
                        }
                        
                    },
                    new ApiHandler.ApiFailureHandler(){
                        
                        @Override
                        public void failure(Throwable t){
                            game.setScreen(new MainMenuScreen(game));
                        }
                    });
            	    transitionTriggers();
            	    Timer.schedule(new Timer.Task(){ @Override public void run() { apiHandler.getRequest("History", "questions"); } }, 5f);
                }
            }
        });

        scienceTechButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isOver()) {
                	game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
                    //Pull database questions for Science and & Technology
                    final ApiHandler apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler(){
                    
                        @Override
                        public void handle(String[][] response){
                            game.setScreen(new GameScreen(game, "Science/Tech", response));
                        }
                        
                    },
                    new ApiHandler.ApiFailureHandler(){
                        
                        @Override
                        public void failure(Throwable t){
                            game.setScreen(new MainMenuScreen(game));
                        }
                    });
            	    transitionTriggers();
            	    Timer.schedule(new Timer.Task(){ @Override public void run() { apiHandler.getRequest("Science-Tech", "questions"); } }, 5f);

                }
            }
        });

        sportsButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isOver()) {
                	game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
                    //Pull database questions for Sports
                    final ApiHandler apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler(){
                    
                        @Override
                        public void handle(String[][] response){
                            game.setScreen(new GameScreen(game, "Sports", response));
                        }
                        
                    },
                    new ApiHandler.ApiFailureHandler(){
                        
                        @Override
                        public void failure(Throwable t){
                            game.setScreen(new MainMenuScreen(game));
                        }
                    });
            	    transitionTriggers();
            	    Timer.schedule(new Timer.Task(){ @Override public void run() { apiHandler.getRequest("Sports", "questions"); } }, 5f);
                }
            }
        });

        gptButton.addListener(new ClickListener() {
            @Override
	        public void clicked(InputEvent event, float x, float y) {
                if (isOver()) {
                	game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
                	mainTable.setVisible(false);
                	inputTable.setVisible(true);
                }
            }
        });
        
        inputSubmitButton.addListener(new ClickListener() {
            @Override
	        public void clicked(InputEvent event, float x, float y) {
                if (isOver()) {
                	game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);

                    String userInput = inputField.getText();
                    if (userInput.length() > 0) { // Could add some real parsing here
	                    ApiHandler apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler(){
	                    
	                        @Override
	                        public void handle(final String[][] response) {
	                    	    transitionTriggers();
	                        	Timer.schedule(new Timer.Task(){ @Override public void run() { game.setScreen(new GameScreen(game, "Wildcard/GPT", response)); } }, 5.5f);
	                        }
	                        
	                    },
                            new ApiHandler.ApiFailureHandler(){
                        
                                @Override
                                public void failure(Throwable t) {
                                	statusMessageFlash.setVisible(false);
                                	statusMessageStart.setText("Question generation failed!");
                                	statusMessageStart.setColor(Color.RED);
                                	statusMessageFlash.setText("");
                                	statusMessageEnd.setText("Returning you to category selection...");
                                    Timer.schedule(new Timer.Task(){ @Override public void run() { game.setScreen(new CategorySelectScreen(game));  } }, 5f);
                                }
                            });
	                    apiHandler.postRequest(userInput);
	                    statusMessageStart.setVisible(true);
	                    statusMessageFlash.setVisible(true);
	                    statusMessageEnd.setVisible(true);
	                    inputSubmitButton.setTouchable(Touchable.disabled);
	                    inputCancelButton.setTouchable(Touchable.disabled);
                    }
                    
                }
            }
        });
        
        inputCancelButton.addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
            	game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
            	inputTable.setVisible(false);
            	mainTable.setVisible(true);
	        }
        });
        
        inputField.addListener(new FocusListener() {
            @Override
            public void keyboardFocusChanged(FocusEvent event, Actor actor, boolean focused) {
                if (focused) {
                    inputField.setText("");
                }
            }
        });
        
        TextField.TextFieldFilter filter = new TextField.TextFieldFilter() {
            private final int charLimit = 20; // 20 is probably more than enough for a category

            @Override
            public boolean acceptChar(TextField textField, char c) {
                return (Character.isLetter(c) || Character.isDigit(c)) && textField.getText().length() < charLimit;
            }
        };
        inputField.setTextFieldFilter(filter);

        // A lot of the values here are eyeballed estimates with the padding and width, which will require refactoring if we want variable resolution support
        // Create a table for our elements and stage them
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.add(chooseCategoryImage).height(60).padTop(25).colspan(4).row(); // 4x4 cell table organized as [Image] [Button] | [Button] [Image]

        mainTable.add(entertainmentImage).width(160).height(180).pad(-20, 0, -20, -35); // Padding is Top, Left, Bottom, Right
        mainTable.add(entertainmentButton).pad(-20, 0, -20, 15);
        mainTable.add(geographyButton).pad(-20, 15, -20, 0);
        mainTable.add(geographyImage).width(160).height(180).pad(-20, -35, -20, 0).row();

        mainTable.add(historyImage).width(160).height(180).pad(-20, 0, -20, -35);
        mainTable.add(historyButton).pad(-20, 0, -20, 15);
        mainTable.add(scienceTechButton).pad(-20, 15, -20, 0);
        mainTable.add(scienceTechImage).width(160).height(180).pad(-20, -35, -20, 0).row();

        mainTable.add(sportsImage).width(160).height(180).pad(-20, 0, -20, -35);
        mainTable.add(sportsButton).pad(-20, 0, -20, 15);
        mainTable.add(gptButton).pad(-20, 15, -20, 0);
        mainTable.add(gptImage).width(160).height(180).pad(-20, -35, -20, 0);
        stage.addActor(mainTable);
        
        // Arrange our secondary table for input prompt
        
        HorizontalGroup group = new HorizontalGroup();
        // Add the labels to the group
        group.addActor(statusMessageStart);
        group.addActor(statusMessageFlash);

        inputTable = new Table();
        inputTable.setFillParent(true);
        inputTable.add(group).colspan(2).row();
        inputTable.add(statusMessageEnd).colspan(2).padBottom(30).row();
        inputTable.add(inputField).padBottom(30).width(200).height(50).colspan(2).row();
        inputTable.add(inputSubmitButton).padRight(10);
        inputTable.add(inputCancelButton).padLeft(10);
        inputTable.setVisible(false);
        stage.addActor(inputTable);
        
	    spotlightStage.addActor(leftSpotlight);
	    spotlightStage.addActor(rightSpotlight);

        // To allow handling input events, set the stage as the input processor
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.spriteBatch.begin();
        if (transition) {
        	game.spriteBatch.draw(mockQuestion, 0, 0, 1200, 800);
        } else {
        	game.spriteBatch.draw(game.background, 0, 0, 1200, 800);
        }
        game.spriteBatch.end();
        // Update t for color flash effect
        t += delta;
        if (t > 1) {
            // If t is greater than 1, reverse the colors and reset t
            Color temp = startColor;
            startColor = endColor;
            endColor = temp;
            t = 0;
        }
        // Find the new color
        Color newColor = startColor.cpy().lerp(endColor, t);
        if (statusMessageFlash != null) { statusMessageFlash.setColor(newColor); }
        stage.act(delta);
        stage.draw();
        spotlightStage.act(delta);
        if (transition) {
	        screenTransition(); // Draw fading over primary stage/BG
        }
        spotlightStage.draw(); // Draw this stage last (foreground)
    }
    
    private void transitionTriggers() { // One-time transition effects
    	 mainTable.setVisible(false);
    	 inputTable.setVisible(false);
		 game.assetManager.get("sounds/lightSwitch.mp3", Sound.class).play(game.soundVolume);
	     game.spriteBatch.begin();
	     game.spriteBatch.setColor(1, 1, 1, 0);
	     game.spriteBatch.draw(fadeTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Set screen to black
	     game.spriteBatch.end();
	     Timer.schedule(new Timer.Task(){ // Move spotlights
	    	    @Override
	    	    public void run() {
	    			transition = true;
	    		    leftSpotlight.speed = 600;
	    		    rightSpotlight.speed = 600;
	    	        game.assetManager.get("sounds/lightsToCentre.mp3", Sound.class).play(game.soundVolume);
	    	    }
	     }, 1.5f); // Delay in seconds
    }
    
    
    private void screenTransition() { // Fade-ins
        game.spriteBatch.begin();
    	if (leftSpotlight.reachedTarget && rightSpotlight.reachedTarget) { 
    		leftSpotlight.alpha -= 0.015f;
    		rightSpotlight.alpha -= 0.015f;
            alpha -= 0.02f;  // Adjust this to control the speed of the fade-in
            if (alpha < 0) {
                alpha = 0;
            }
            game.spriteBatch.setColor(1, 1, 1, alpha); // Set the SpriteBatch color
            game.spriteBatch.draw(fadeTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()); // Draw the fade texture over the whole screen
            game.spriteBatch.setColor(1, 1, 1, 1); // Reset the color
    	}
        game.spriteBatch.end();
    }
    
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // Preserves aspect ratio upon resize
    }

    @Override
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

}
