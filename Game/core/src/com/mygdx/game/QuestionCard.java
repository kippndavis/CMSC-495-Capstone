/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;

/**
 *
 * @author chris
 */
public class QuestionCard {

    final private Table questionTable;
    final private Table answerCard;
    final protected TextButton questionBox;
    final protected AnswerButton[] answerBoxes;
    final protected String[][] questionData;
    final protected Label timerLabel;
    final protected Label scoreLabel;
    final private LabelStyle labelStyle;
    protected int timeRemaining;
    protected int userScore;
    
    public QuestionCard(SpriteDrawable[] sprites, TextButtonStyle[] styles, String[][] questionPool, LabelStyle labelStyle) {
    	
    	//gameScreen = gameScr;
    	questionData = questionPool;
    	//create main question card table
        questionTable = new Table();
        questionTable.setFillParent(true);
        
        //add question box
        questionBox = new TextButton("Question", styles[0]);
        questionBox.getStyle().font = new BitmapFont();
        questionBox.getStyle().up = sprites[0];
        questionBox.getLabel().setWrap(true);
        questionBox.getCell(questionBox.getLabel()).width(600);//determines how long the text can be before wrapping
        
        questionTable.add(questionBox).row();
        
        answerCard = new Table();
        answerBoxes = new AnswerButton[4];
    
        //add timer and score
        timeRemaining = 30;
        userScore = 0;
        this.labelStyle = labelStyle;
        timerLabel = new Label(Integer.toString(timeRemaining), labelStyle);
        scoreLabel = new Label(Integer.toString(userScore), labelStyle);
        questionTable.add(timerLabel).padTop(-2).row();
        questionTable.add(scoreLabel).row();
        
        
        //add answer buttons
        for(int i = 0; i < answerBoxes.length; i++){
        	// Answers stop fitting text boxes after 38 characters; add scaling if this is an issue?
            answerBoxes[i] = new AnswerButton(sprites, "", styles[i+1]);
            answerCard.add(answerBoxes[i]).size(375, 50).pad(80, 5, 0, 5);
            if(i == 1){
                answerCard.row();
            }
        }

        questionTable.add(answerCard);  
    }
    
    public Table getQuestionTable(){
        return questionTable;
    }
    
    /*
     tracks and manages state of buttons for use in gamescreen class
    */
    public boolean[] manageButtons() {
        boolean answerChosen = false;
        boolean isCorrect = false;
        for(int i = 0; i < answerBoxes.length; i++) {
            if (answerBoxes[i].isClicked == true) {
                if(answerBoxes[i].isCorrectAnswer) {
                    isCorrect = true;
                }
                answerChosen = true;
                break;
            }
        }
        
        if(answerChosen == true) {
            for(AnswerButton button : answerBoxes) {
                button.isActive = false;
            }
        }
        boolean[] choice = {answerChosen, isCorrect};
        return choice;
    }
}
