package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class TriviaGame extends Game {
    
    public AssetManager assetManager;
    public SpriteBatch spriteBatch;
    public Texture background;
    public BitmapFont copperplateFont;
    public BitmapFont itcFont;
    public static TriviaGame INSTANCE;
    public float soundVolume = 0.10f;
    
    public TriviaGame() {
    	INSTANCE = this;
    }

    @Override
    public void create() {
    	assetManager = new AssetManager();
    	spriteBatch = new SpriteBatch();
    	assetManager.load("graphics/background.png", Texture.class);
    	assetManager.load("fonts/CopperplateGothicLight.fnt", BitmapFont.class);
    	assetManager.load("fonts/ITCConduit.fnt", BitmapFont.class);
    	assetManager.finishLoading();
    	background = assetManager.get("graphics/background.png", Texture.class);
    	copperplateFont = assetManager.get("fonts/CopperplateGothicLight.fnt", BitmapFont.class);
    	itcFont = assetManager.get("fonts/ITCConduit.fnt", BitmapFont.class);
    	setScreen(new MainMenuScreen(INSTANCE));
    }

    @Override
    public void dispose () {
        assetManager.dispose();
        spriteBatch.dispose();
    }
}