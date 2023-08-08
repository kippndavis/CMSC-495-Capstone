package com.mygdx.game;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class MainMenuScreen extends ScreenAdapter {
	
	TriviaGame game = TriviaGame.INSTANCE;
	Viewport viewport;
	Stage stage;
	Label volumeLabel;
	
	public MainMenuScreen(TriviaGame game) {
		this.game = game;
	}
	
	@Override
	public void show() {
		
		// Load the assets required for this screen
		game.assetManager.load("graphics/Logo.png", Texture.class);
		game.assetManager.load("graphics/ButtonUp.png", Texture.class);
		game.assetManager.load("graphics/ButtonDown.png", Texture.class);
		game.assetManager.load("graphics/SmallButtonUp.png", Texture.class);
		game.assetManager.load("graphics/SmallButtonDown.png", Texture.class);
		game.assetManager.load("sounds/buttonClick.mp3", Sound.class);
		game.assetManager.finishLoading(); // Blocks until assets are done loading

		viewport = new FitViewport(800, 480);
		stage = new Stage(viewport);
		
		Texture logoTexture = game.assetManager.get("graphics/Logo.png", Texture.class);
		Image logoImage = new Image(logoTexture);
		logoImage.setOrigin(logoImage.getWidth() / 2, logoImage.getHeight() / 2);
		logoImage.setScale(0.60f);
	
		// Define fonts and pictures, and styles for buttons
		Sprite buttonUpSprite = new Sprite(game.assetManager.get("graphics/ButtonUp.png", Texture.class));
		buttonUpSprite.setSize(150, 75);
		Sprite buttonDownSprite = new Sprite(game.assetManager.get("graphics/ButtonDown.png", Texture.class));
		buttonDownSprite.setSize(150, 75);
		Sprite smallButtonUpSprite = new Sprite(game.assetManager.get("graphics/SmallButtonUp.png", Texture.class));
		smallButtonUpSprite.setSize(50, 50);
		Sprite smallButtonDownSprite = new Sprite(game.assetManager.get("graphics/SmallButtonDown.png", Texture.class));
		smallButtonDownSprite.setSize(50, 50);
		TextButton.TextButtonStyle menuButtonStyle = new TextButton.TextButtonStyle();
		menuButtonStyle.font = game.itcFont;
		menuButtonStyle.font.getData().setScale(0.65f);
		menuButtonStyle.up = new SpriteDrawable(buttonUpSprite);
		menuButtonStyle.down = new SpriteDrawable(buttonDownSprite);
		TextButton.TextButtonStyle volumeButtonStyle = new TextButton.TextButtonStyle();
		volumeButtonStyle.font = game.itcFont;
		volumeButtonStyle.up = new SpriteDrawable(smallButtonUpSprite);
		volumeButtonStyle.down = new SpriteDrawable(smallButtonDownSprite);
		
		// Create volume label
        LabelStyle labelStyle = new LabelStyle(game.itcFont, Color.WHITE);
        volumeLabel = new Label("Volume: " + (int)(game.soundVolume * 100) + "%", labelStyle);
	
		// Create TextButtons with their respective styles
		final TextButton playButton = new TextButton("Play Game", menuButtonStyle);
		final TextButton highScoreButton = new TextButton("High Scores", menuButtonStyle);
		final TextButton volumeUpButton = new TextButton("+", volumeButtonStyle);
		final TextButton volumeDownButton = new TextButton("-", volumeButtonStyle);
	
	    playButton.addListener(new ClickListener() {
	        @Override
	        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
	        	 if (isOver()) { 
	        		 game.setScreen(new CategorySelectScreen(game));
	        		 game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
	        	 }
	        }
	    });
	    
	    highScoreButton.addListener(new ClickListener() {
	        @Override
	        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
	        	 if (isOver()) { 
	        		 game.setScreen(new LeaderboardScreen(game));
	        		 game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
	        	 }
	        }
	    });
	    
	    volumeUpButton.addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	            if (game.soundVolume < 1f) { game.soundVolume = Math.round((game.soundVolume + 0.10f) * 10) / 10.0f; }
	            volumeLabel.setText("Volume: " + (int)(game.soundVolume * 100) +"%");
	            game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
	        }
	    });
	    
	    volumeDownButton.addListener(new ClickListener() {
	        @Override
	        public void clicked(InputEvent event, float x, float y) {
	            if (game.soundVolume > 0f) { game.soundVolume = Math.round((game.soundVolume - 0.10f) * 10) / 10.0f; }
	            volumeLabel.setText("Volume: " + (int)(game.soundVolume * 100) +"%");
	            game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
	        }
	    });
	    
	    // Create a table for our elements and stage them
	    Table mainTable = new Table();
	    mainTable.setFillParent(true);
	    mainTable.add(logoImage).colspan(3).padTop(-15).row(); // Use colspan to center logo image
	    mainTable.add(playButton).colspan(3).pad(0, 0, 15, 0).row();
	    mainTable.add(highScoreButton).colspan(3).pad(15, 0, 40, 0).row();
	    mainTable.add(volumeUpButton).padRight(-300);
	    mainTable.add(volumeLabel);
	    mainTable.add(volumeDownButton).padLeft(-300);
	    stage.addActor(mainTable);
	    //Spotlight(float x, float y, float sourceX, float sourceY, float targetX, float targetY, float speed, float radius, float lineWidthSource, float lineWidthPosition) {
	    Spotlight leftSpotlight = new Spotlight(-300, 300, -50, 800, 300, 300, 400, 135, 50, 115);
	    Spotlight rightSpotlight = new Spotlight(1500, 300, 1250, 800, 900, 300, 400, 135, 50, 115);
	    stage.addActor(leftSpotlight);
	    stage.addActor(rightSpotlight);
		
	    // To allow handling input events, set the stage as the input processor
	    Gdx.input.setInputProcessor(stage);
	    
	}
	
	@Override
	public void render(float delta) {
	    Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
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
    public void hide(){
        Gdx.input.setInputProcessor(null);
    }
	
	
}