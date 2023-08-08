package com.mygdx.game;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.FocusListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class FinishScreen extends ScreenAdapter {

    TriviaGame game = TriviaGame.INSTANCE;
    Viewport viewport;
    Stage stage;
    String finishMessage;
    String category;
    int userScore;
    Table mainTable;
    Table inputTable;
    private TextField inputField;

    public FinishScreen(TriviaGame game, String finishMessage, String category, int userScore) {
        this.finishMessage = finishMessage;
        this.category = category;
        this.userScore = userScore;
    }

    @Override
    public void show() {

        // Load the assets required for this screen
        game.assetManager.load("graphics/ButtonUp.png", Texture.class);
        game.assetManager.load("graphics/ButtonDown.png", Texture.class);
        game.assetManager.load("graphics/textFieldBackground.png", Texture.class);
        game.assetManager.load("graphics/textFieldFocusedBackground.png", Texture.class);
        game.assetManager.load("graphics/textFieldCursor.png", Texture.class);
        game.assetManager.load("graphics/textFieldSelection.png", Texture.class);
        game.assetManager.load("sounds/buttonClick.mp3", Sound.class);
        game.assetManager.finishLoading(); // Blocks until assets are done loading

        viewport = new FitViewport(800, 480);
        stage = new Stage(viewport);

        // Create labels for user result and score
        LabelStyle labelStyle = new LabelStyle(game.itcFont, Color.WHITE);
        Label outcomeLabel = new Label(finishMessage, labelStyle);
        outcomeLabel.setFontScale(1.3f);
        Label scoreLabel = new Label("Final score: " + userScore + " points", labelStyle);
        Label categoryLabel = new Label("Category: " + category, labelStyle);

        // Define fonts and pictures, and styles for buttons
        Sprite buttonUpSprite = new Sprite(game.assetManager.get("graphics/ButtonUp.png", Texture.class));
        buttonUpSprite.setSize(150, 75);
        Sprite buttonDownSprite = new Sprite(game.assetManager.get("graphics/ButtonDown.png", Texture.class));
        buttonDownSprite.setSize(150, 75);
        TextButton.TextButtonStyle menuButtonStyle = new TextButton.TextButtonStyle();
        menuButtonStyle.font = game.itcFont;
        menuButtonStyle.up = new SpriteDrawable(buttonUpSprite);
        menuButtonStyle.down = new SpriteDrawable(buttonDownSprite);

        // Create TextButtons with their respective styles
        final TextButton mainMenuButton = new TextButton("Main Menu", menuButtonStyle);
        final TextButton submitScoreButton = new TextButton("Save Score", menuButtonStyle);
        final TextButton inputSubmitButton = new TextButton("Submit", menuButtonStyle);

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
        inputField = new TextField("Enter initials...", textFieldStyle);
        inputField.setAlignment(Align.center);
        
        mainMenuButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isOver()) {
                    game.setScreen(new MainMenuScreen(game));
                    game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
                }
            }
        });

        submitScoreButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isOver()) {
                    // Some database action
                	game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
                	mainTable.setVisible(false);
                	inputTable.setVisible(true);
                }
            }
        });
        
        inputSubmitButton.addListener(new ClickListener() {
            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (isOver()) {
                	game.assetManager.get("sounds/buttonClick.mp3", Sound.class).play(game.soundVolume);
                    String initials = inputField.getText();
                    
                    //Submit scores and return to menu
                    ApiHandler apiHandler = new ApiHandler(new ApiHandler.ApiResponseHandler(){
                    
                        @Override
                        public void handle(String[][] response){
                            game.setScreen(new MainMenuScreen(game));
                        }
                        
                    },
                    new ApiHandler.ApiFailureHandler(){
                        
                        @Override
                        public void failure(Throwable t){
                            //do failure stuff
                        }
                    });
                    apiHandler.postRequest(category, initials, userScore);
                }
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
            private final int charLimit = 3;

            @Override
            public boolean acceptChar(TextField textField, char c) {
                return Character.isLetter(c) && textField.getText().length() < charLimit; // Only permit 3 letters
            }
        };
        inputField.setTextFieldFilter(filter);

        // Create a table for our elements and stage them
        mainTable = new Table();
        mainTable.setFillParent(true);
        mainTable.add(outcomeLabel).padBottom(10).center().row();
        mainTable.add(scoreLabel).padBottom(10).center().row();
        mainTable.add(categoryLabel).padBottom(50).center().row();
        mainTable.add(mainMenuButton).pad(20, 0, 20, 0).row();
        mainTable.add(submitScoreButton).pad(20, 0, 20, 0);
        stage.addActor(mainTable);
        
        // Arrange our secondary table for input prompt
        inputTable = new Table();
        inputTable.setFillParent(true);
        inputTable.add(inputField).padBottom(30).width(200).height(50).colspan(2).row();
        inputTable.add(inputSubmitButton);
        inputTable.setVisible(false);
        stage.addActor(inputTable);


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
    public void hide() {
        Gdx.input.setInputProcessor(null);
    }

}