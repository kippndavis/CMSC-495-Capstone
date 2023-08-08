/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.Timer;

public class AnswerButton extends TextButton {
    protected boolean isActive;
    protected boolean isClicked;
    protected boolean isCorrectAnswer;
    final SpriteDrawable[] sprites;
    
    public AnswerButton(SpriteDrawable[] sprites, String answerData, TextButtonStyle style){
        super(answerData, style);
        this.sprites = sprites;
        this.getStyle().font = new BitmapFont();
        this.getStyle().up = this.sprites[0];
        this.getLabel().setWrap(true);
        this.getCell(this.getLabel()).width(300); 

        this.isActive = true;
        this.isClicked = false;
        this.isCorrectAnswer = false;      
        
        this.addListeners(); 
    }
    
    //add event listeners for clicks and other button behavior
    private void addListeners() {
        
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                
                if(isActive) {
                    getStyle().up = sprites[4];
                    invalidate();
                    isClicked = true;

                    Timer.schedule(new Timer.Task(){
                        @Override
                        public void run(){
                            if (isCorrectAnswer == true && isActive != true) {
                                getStyle().up = sprites[2];
                                invalidate();
                            } else if(isActive != true){
                                getStyle().up = sprites[3];
                                invalidate();
                            }
                            invalidate();
                        }
                    }, 3f);
                }
            }
        });
        
        // event listener for hover effect
        addListener(new InputListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                if(isClicked != true && isActive == true){
                    getStyle().up = sprites[1];    
                }
            }   

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                if(isClicked != true && isActive == true){
                    getStyle().up = sprites[0];
                }
            }
        });
    } // end of addListeners
    
}
