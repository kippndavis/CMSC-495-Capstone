package com.mygdx.game;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.List;
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class LeaderboardScreen extends ScreenAdapter {

    TriviaGame game = TriviaGame.INSTANCE;
    Viewport viewport;
    Stage stage;
    String[][] scores;
    ArrayList<Label> labelsList;
    HashMap<String, String[][]> scoreCategoryMap;
    SelectBox<String> selectBox;

    public LeaderboardScreen(TriviaGame game) {
        this.game = game;
    }
    
    // Helper method to reduce duplication
    private Image createImage(String asset, float newScaleX, float newScaleY) {
        Texture texture = game.assetManager.get(asset, Texture.class);
        Image image = new Image(texture);
        float newOriginX = texture.getWidth() / 2 * newScaleX;
        float newOriginY = texture.getHeight() / 2 * newScaleY;
        image.setOrigin(newOriginX, newOriginY);
        image.setScale(newScaleX, newScaleY);
        return image;
    }
    
    private void updateLabelsList(String[][] scoreArray) {
        int index = 0;
        for (Label label : labelsList) {
            // Update with rank, player name, and score
            label.setText((index + 1) + "    " + scoreArray[index][1] + "    " + scoreArray[index][0]);
            index++;
        }
    }

    @Override
    public void show() {
        
        // Go get the scores for each category
        labelsList = new ArrayList<>();
        scoreCategoryMap = new HashMap<String, String[][]>();

        ApiHandler apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler() {
            @Override
            public void handle(String[][] response) {
                scoreCategoryMap.put("entertainment", response);
                updateLabelsList(response); // Fill out our table initially
            }
        },
        new ApiHandler.ApiFailureHandler(){
                        
            @Override
            public void failure(Throwable t){
                //do failure stuff
            }
        });
        
        apiHandler.getRequest("entertainment", "scores");
        
        apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler() {
            @Override
            public void handle(String[][] response) {
                scoreCategoryMap.put("geography", response);
            }
        },
        new ApiHandler.ApiFailureHandler(){
                        
            @Override
            public void failure(Throwable t){
                //do failure stuff
            }
        });
        
        apiHandler.getRequest("geography", "scores");
        
        apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler() {
            @Override
            public void handle(String[][] response) {
                scoreCategoryMap.put("history", response);
            }
        },
        new ApiHandler.ApiFailureHandler(){
                        
            @Override
            public void failure(Throwable t){
                //do failure stuff
            }
        });
        
        apiHandler.getRequest("history", "scores");
        
        apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler() {
            @Override
            public void handle(String[][] response) {
                scoreCategoryMap.put("science-tech", response);
            }
        },
        new ApiHandler.ApiFailureHandler(){
                        
            @Override
            public void failure(Throwable t){
                //do failure stuff
            }
        });
        
        apiHandler.getRequest("science-tech", "scores");
        
        apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler() {
            @Override
            public void handle(String[][] response) {
                scoreCategoryMap.put("sports", response);
            }
        },
        new ApiHandler.ApiFailureHandler(){
                        
            @Override
            public void failure(Throwable t){
                //do failure stuff
            }
        });
        
        apiHandler.getRequest("sports", "scores");
        
        apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler() {
            @Override
            public void handle(String[][] response) {
                scoreCategoryMap.put("wildcard-gpt", response);
            }
        },
        new ApiHandler.ApiFailureHandler(){
                        
            @Override
            public void failure(Throwable t){
                //do failure stuff
            }
        });
        
        apiHandler.getRequest("wildcard-gpt", "scores");

        // Load the assets required for this screen
        game.assetManager.load("graphics/ButtonUp.png", Texture.class);
        game.assetManager.load("graphics/ButtonDown.png", Texture.class);
        game.assetManager.load("graphics/leaderboards.png", Texture.class);
        game.assetManager.load("graphics/medalGoldIcon.png", Texture.class);
        game.assetManager.load("graphics/medalSilverIcon.png", Texture.class);
        game.assetManager.load("graphics/medalBronzeIcon.png", Texture.class);
        game.assetManager.load("graphics/medalAlphaIcon.png", Texture.class);
        game.assetManager.load("graphics/scrollKnob.png", Texture.class);
        game.assetManager.load("graphics/selectBoxBackground.png", Texture.class);
        game.assetManager.load("graphics/selectBoxBackgroundOpen.png", Texture.class);
        game.assetManager.load("graphics/selectBoxBackgroundOver.png", Texture.class);
        game.assetManager.load("graphics/selectBoxScrollableBackground.png", Texture.class);
        game.assetManager.load("graphics/textFieldSelection.png", Texture.class);
        game.assetManager.load("sounds/buttonClick.mp3", Sound.class);
        game.assetManager.finishLoading(); // Blocks until assets are done loading

        viewport = new FitViewport(800, 480);
        stage = new Stage(viewport);
        
        Image leaderboardImage = createImage("graphics/leaderboards.png", 0.8f, 1f);
        Image goldMedalImage = createImage("graphics/medalGoldIcon.png", 1f, 1f);
        Image silverMedalImage = createImage("graphics/medalSilverIcon.png", 1f, 1f);
        Image bronzeMedalImage = createImage("graphics/medalBronzeIcon.png", 1f, 1f);
        Image alphaMedalImage = createImage("graphics/medalAlphaIcon.png", 1f, 1f);
        
		// Define fonts, pictures and styles for buttons
		Sprite buttonUpSprite = new Sprite(game.assetManager.get("graphics/ButtonUp.png", Texture.class));
		buttonUpSprite.setSize(150, 75);
		Sprite buttonDownSprite = new Sprite(game.assetManager.get("graphics/ButtonDown.png", Texture.class));
		buttonDownSprite.setSize(150, 75);
		TextButton.TextButtonStyle menuButtonStyle = new TextButton.TextButtonStyle();
		menuButtonStyle.font = game.itcFont;
		menuButtonStyle.up = new SpriteDrawable(buttonUpSprite);
		menuButtonStyle.down = new SpriteDrawable(buttonDownSprite);
	
		final TextButton backButton = new TextButton("Main Menu", menuButtonStyle);
	    backButton.addListener(new ClickListener() {
	        @Override
	        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
	        	 if (isOver()) { 
	        		 game.setScreen(new MainMenuScreen(game));
	        		 game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
	        	 }
	        }
	    });

        // Create the text field assets, style & object
        Texture selectBoxBackgroundTexture = game.assetManager.get("graphics/selectBoxBackground.png", Texture.class);
        Texture selectBoxBackgroundOverTexture = game.assetManager.get("graphics/selectBoxBackgroundOver.png", Texture.class);
        Texture selectBoxScrollBackgroundTexture = game.assetManager.get("graphics/selectBoxScrollableBackground.png", Texture.class);
        TextureRegionDrawable selectBoxBackground = new TextureRegionDrawable(new TextureRegion(selectBoxBackgroundTexture));
        TextureRegionDrawable selectBoxBackgroundOver = new TextureRegionDrawable(new TextureRegion(selectBoxBackgroundOverTexture));
        TextureRegionDrawable selectBoxScrollBackground = new TextureRegionDrawable(new TextureRegion(selectBoxScrollBackgroundTexture));
        SelectBox.SelectBoxStyle selectBoxStyle = new SelectBox.SelectBoxStyle();
        selectBoxStyle.font = game.itcFont;
        selectBoxStyle.fontColor = Color.WHITE;
        selectBoxStyle.overFontColor = Color.WHITE;
        selectBoxStyle.background = selectBoxBackground;
        selectBoxStyle.backgroundOpen = selectBoxBackgroundOver;
        selectBoxStyle.backgroundOver = selectBoxBackgroundOver;
        
        // Add missing listStyle to selectBoxStyle
        List.ListStyle listStyle = new List.ListStyle();
        listStyle.font = game.itcFont;
        listStyle.fontColorUnselected = Color.WHITE;
        listStyle.fontColorSelected = Color.WHITE;
        listStyle.selection = new TextureRegionDrawable(new TextureRegion(new Texture("graphics/textFieldSelection.png"))); // replace this with actual texture
        selectBoxStyle.listStyle = listStyle;
        
        // Add scrollStyle to selectBoxStyle
        ScrollPane.ScrollPaneStyle selectBoxScrollStyle = new ScrollPane.ScrollPaneStyle();
        selectBoxScrollStyle.background = selectBoxScrollBackground;
        selectBoxScrollStyle.vScroll = new TextureRegionDrawable(new TextureRegion(new Texture("graphics/textFieldSelection.png")));
        selectBoxScrollStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(new Texture("graphics/textFieldSelection.png")));
        selectBoxStyle.scrollStyle = selectBoxScrollStyle;

        
        selectBox = new SelectBox<String>(selectBoxStyle);
        selectBox.setItems("Entertainment", "Geography", "History", "Science & Tech", "Sports", "Wildcard/GPT");
        selectBox.setAlignment(Align.center); 
        selectBox.setDisabled(true); // Initially, disable the SelectBox while asynchronous calls finish
        
        selectBox.addListener((EventListener) new ChangeListener() {
            @Override
            public void changed (ChangeEvent event, Actor actor) {
                String selectedCategory = selectBox.getSelected().toLowerCase();
                if (selectedCategory.equals("science & tech")) { selectedCategory = "science-tech"; }
                if (selectedCategory.equals("wildcard/gpt")) { selectedCategory = "wildcard-gpt"; }
                if (scoreCategoryMap.containsKey(selectedCategory)) {
                    updateLabelsList(scoreCategoryMap.get(selectedCategory.toLowerCase()));
                }
            }
        });
        
        // Create a table for our elements and stage them
        Table mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.add(leaderboardImage).padTop(25).height(60).row();
        mainTable.add(selectBox).padTop(10).width(200).height(35).row();
        
        // Create a table to hold the labels
        Table labelTable = new Table();
        labelTable.padRight(30);

        // Create the labels and add them to the table
        LabelStyle labelStyleNormal = new LabelStyle(game.itcFont, Color.WHITE);
        LabelStyle labelStyleGold = new LabelStyle(game.itcFont, new Color(251/255f, 215/255f, 52/255f, 1)); // Gold
        LabelStyle labelStyleSilver = new LabelStyle(game.itcFont, new Color(181/255f, 181/255f, 172/255f, 1)); // Silver
        LabelStyle labelStyleBronze = new LabelStyle(game.itcFont, new Color(205/255f, 127/255f, 50/255f, 1)); // Bronze
        LabelStyle currentLabelStyle;
        for (int i = 0; i < 25; i++) {
            // Create a string with the rank, player name, and score
            String labelText = (i+1) + "    " + "???" + "    " + "???";

            switch (i) {
            case (0):
            	currentLabelStyle = labelStyleGold;
            	labelTable.add(goldMedalImage).pad(0, 0, 10f, 10f);
            	break;
            case (1):
            	currentLabelStyle = labelStyleSilver;
            	labelTable.add(silverMedalImage).pad(0, 0, 10f, 10f);
            	break;
            case (2):
            	currentLabelStyle = labelStyleBronze;
            	labelTable.add(bronzeMedalImage).pad(0, 0, 10f, 10f);
            	break;
            default:
            	currentLabelStyle = labelStyleNormal;
            	labelTable.add(alphaMedalImage).pad(0, 0, 0, 0f);
            	break;
            }
            Label label = new Label(labelText, currentLabelStyle);
            labelsList.add(label);
            label.setAlignment(Align.left);
            labelTable.add(label).row();

        }

        // Create the ScrollPane and add the table to it
        ScrollPane scrollPane = new ScrollPane(labelTable);
        ScrollPane.ScrollPaneStyle leaderboardScrollStyle = new ScrollPane.ScrollPaneStyle();
        leaderboardScrollStyle.vScrollKnob = new TextureRegionDrawable(new TextureRegion(new Texture("graphics/scrollKnob.png")));
        scrollPane.setStyle(leaderboardScrollStyle);

        // Add the ScrollPane to a stage or main table
        mainTable.add(scrollPane).pad(10, 0, 15, 0).row();
        mainTable.add(backButton).padBottom(15);

        // Other settings for the ScrollPane
        scrollPane.setScrollingDisabled(false, false); // Enable horizontal and vertical scrolling
        scrollPane.setOverscroll(false, false);
        scrollPane.setFadeScrollBars(false);

        stage.addActor(mainTable);

        // To allow handling input events, set the stage as the input processor
        Gdx.input.setInputProcessor(stage);

    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if (scoreCategoryMap.size() == 6) { // Check if all categories are loaded
            selectBox.setDisabled(false); // Enable the SelectBox
        }
        game.spriteBatch.begin();
        game.spriteBatch.draw(game.background, 0, 0, 1200, 800);
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
    }

}