package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

public class Confetti {

    public static final int WIDTH = 4;
    public static final int HEIGHT = 4; 

    private static final Texture texture;
    private static final Color[] COLORS = new Color[] {
        Color.RED,
        Color.PINK,
        Color.BLUE,
        Color.CYAN,
        Color.YELLOW,
        Color.GREEN
    };

    static { // Define a 1x1 texture for all confettis to share
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        texture = new Texture(pixmap);
        pixmap.dispose();
    }

    private float x, y;
    private float xSpeed, ySpeed;
    private Color color; 

    // Create a new piece of confetti at a given position, speed and a random color from the preset
    public Confetti(float x, float y, float xSpeed, float ySpeed) {
        this.x = x;
        this.y = y;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
        this.color = COLORS[MathUtils.random(COLORS.length - 1)];
    }

    public float getY() {
    	return this.y;
    }

    // Update the confetti's position based on its velocity
    public void update() {
        x += xSpeed;
        y += ySpeed;
    }

    // Draw the confetti
    public void draw(SpriteBatch batch) {
        batch.setColor(this.color);
        batch.draw(texture, x, y, WIDTH, HEIGHT);
        batch.setColor(Color.WHITE);
    }
}