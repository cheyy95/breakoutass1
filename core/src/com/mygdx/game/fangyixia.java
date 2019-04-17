package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.BodyDef;

/**
 *
 * @author sraven0
 */
public class MenuScreen implements Screen {

    Texture img;

    MyGdxGame game;

    Sprite logo;

    MenuScreen(MyGdxGame game) {
        this.game = game;
        img = new Texture("badlogic.jpg");
        logo = new Sprite(img);
        logo.setPosition(
                Gdx.graphics.getWidth() / 2 - logo.getWidth() / 2,
                Gdx.graphics.getHeight() / 2 - logo.getHeight() / 2);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        game.batch.begin();

        game.batch.draw(logo, logo.getX(), logo.getY());

        game.font.setColor(1, 0, 0, 1);
        game.font.draw(game.batch, "Welcome to Breakout!!! ",
                logo.getX(), logo.getY() + logo.getHeight() + 50);
        game.font.draw(game.batch, "Tap anywhere to begin!",
                logo.getX(), logo.getY() - 50);

        game.batch.end();

        if (Gdx.input.justTouched() || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            game.setScreen(new GameScreen(game));
            dispose();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        img.dispose();
    }

}

    private void createBricks() {
        int brickWidth = brickTex.getWidth();
        int brickHeight = brickTex.getHeight();
        brickWidth += brickHPad * 2;
        brickHeight += brickVPad * 2;
        // number of rows and columns of bricks that can be made
        int numCols = screenWidth / brickWidth;
        int numRows = (screenHeight - areaPad) / brickHeight;

        for (int i = 0; i < numCols; i++) {
            for (int j = 0; j < numRows; j++) {
                int col = i * brickWidth;
                int row = screenHeight - brickHeight - (j * brickHeight);


                PhysicalSprite.Defs defs = PhysicalSprite.Defs.fromScreenCoordinates(
                        brickTex, col, row, WORLD_SCALE);

                defs.bodyDef.type = BodyDef.BodyType.DynamicBody;

                defs.fixtureDef.density = 0.1f;
                defs.fixtureDef.filter.categoryBits = BRICK;
                defs.fixtureDef.filter.maskBits = (BALL | WORLD_BOUND);

                PhysicalSprite brick = new PhysicalSprite(defs, world);

                bricks.add(brick);
            }
        }
    }

    private void createBricks() {

        int brickWidth = brickTex.getWidth();
        int brickHeight = brickTex.getHeight();
        //brickWidth += brickHPad * 2;
        //brickHeight += brickVPad * 2;
        // number of rows and columns of bricks that can be made
        int x = 20;
        int y = 4;

        for (int i = 0; i < x; i++) {
            for (int j = 0; j < y; j++) {
                int col = i * brickWidth;
                int row = screenHeight - brickHeight - (j * brickHeight);
                //int row = 200;


                PhysicalSprite.Defs defs = PhysicalSprite.Defs.fromScreenCoordinates(
                        brickTex, col, row, WORLD_SCALE);

                defs.bodyDef.type = BodyDef.BodyType.DynamicBody;

                defs.fixtureDef.density = 0.1f;
                defs.fixtureDef.filter.categoryBits = BRICK;
                defs.fixtureDef.filter.maskBits = (BALL | WORLD_BOUND);

                PhysicalSprite brick = new PhysicalSprite(defs, world);

                bricks.add(brick);
            }
        }
    }


public class PausedScreen implements Screen {

    MyGdxGame game;
    Screen returngame;

    public PausedScreen(MyGdxGame game, Screen returnTo) {
        this.game = game;
        this.returngame = returnTo;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            game.setScreen(returngame);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.setColor(1, 0, 0, 1);
        game.font.draw(game.batch, "Game Paused! Enter P to retrun gamescreen",
                150,
                Gdx.graphics.getHeight() / 2);
        game.batch.end();

    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
    }

}