package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class Spotlight extends Actor {

    public Vector2 position;
    private Vector2 target;
    private Vector2 source;
    public float speed;
    private float radius;
    private float lineWidthSource;
    private float lineWidthPosition;
    private ShapeRenderer shapeRenderer;
    private Color sourceColor;
    private Color color;
    public boolean reachedTarget;
    public float alpha = 1f;

    public Spotlight(float x, float y, float sourceX, float sourceY, float targetX, float targetY, float speed, float radius, float lineWidthSource, float lineWidthPosition) {
        this.position = new Vector2(x, y);
        this.source = new Vector2(sourceX, sourceY); // Where the light column begins
        this.target = new Vector2(targetX, targetY); // Where the spotlight is going
        this.speed = speed;
        this.radius = radius;
        this.lineWidthSource = lineWidthSource;
        this.lineWidthPosition = lineWidthPosition;
        this.shapeRenderer = new ShapeRenderer();
        this.sourceColor = new Color(1, 1, 1, alpha - 0.5f);
        this.color = new Color(1, 1, 1, alpha);
        this.reachedTarget = false;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);

        // Draw the shadow ellipse, slightly offset
        float shadowOffset = 7;
        shapeRenderer.ellipse(position.x - radius * 0.75f, position.y - radius * 0.5f - shadowOffset, radius * 1.5f, radius); 
        
        shapeRenderer.setColor(this.sourceColor); // 50% alpha

        Vector2 dir = new Vector2(position.x - source.x, position.y - source.y).nor();
        Vector2 perp = new Vector2(-dir.y, dir.x);
        

        // Adjust the trapezoid ends to create a wider column of light to match the ellipse
        Vector2 bottomLeft = new Vector2(source.x, source.y).add(new Vector2(perp).scl(lineWidthSource / 2));
        Vector2 bottomRight = new Vector2(source.x, source.y).sub(new Vector2(perp).scl(lineWidthSource / 2));
        Vector2 topLeft = new Vector2(position.x, position.y).add(new Vector2(perp).scl((lineWidthPosition * 1.5f) / 2)); 
        Vector2 topRight = new Vector2(position.x, position.y).sub(new Vector2(perp).scl((lineWidthPosition * 1.5f) / 2));

        // Draw the trapezoid using two triangles
        shapeRenderer.triangle(bottomLeft.x, bottomLeft.y, bottomRight.x, bottomRight.y, topLeft.x, topLeft.y);
        shapeRenderer.triangle(topLeft.x, topLeft.y, bottomRight.x, bottomRight.y, topRight.x, topRight.y);

        // Draw the ellipse
        shapeRenderer.setColor(this.color);
        shapeRenderer.ellipse(position.x - radius * 0.75f, position.y - radius * 0.5f, radius * 1.5f, radius); 
        shapeRenderer.end();

        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        
        this.sourceColor = new Color(1, 1, 1, alpha - 0.5f);
        this.color = new Color(1, 1, 1, alpha);

        Vector2 direction = new Vector2(target.x - position.x, target.y - position.y);
        if (direction.len() > speed * delta) {
            direction.nor().scl(speed * delta);
            position.add(direction);
        } else {
            position.set(target);
            this.reachedTarget = true;
        }
    }

    public void dispose() {
        shapeRenderer.dispose();
    }
}