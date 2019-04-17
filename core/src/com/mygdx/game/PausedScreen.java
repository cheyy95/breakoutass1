package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;

/**
 *
 * @author sraven0
 */
public class PausedScreen implements Screen {

    MyGdxGame game;
    Screen returnTo;

    public PausedScreen(MyGdxGame game, Screen returnTo) {
        this.game = game;
        this.returnTo = returnTo;
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            game.setScreen(returnTo);
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.setColor(1, 0, 0, 1);
        game.font.draw(game.batch, "Paused",
                Gdx.graphics.getWidth() / 2,
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