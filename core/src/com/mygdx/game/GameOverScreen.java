package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author sraven0
 */
public class GameOverScreen implements Screen {

    public static final String filename = "breakoutdemo";
    private static final int LINE_SPACE = 15;

    MyGdxGame game;
    int score;
    List<Integer> scores;

    public GameOverScreen(MyGdxGame game, int score) {
        this.game = game;
        this.score = score;
        scores = new ArrayList<>();
        scores.add(score);
    }

    //TODO add name to high score
    //https://github.com/libgdx/libgdx/wiki/Simple-text-input
    private void read() {
        try {
            FileHandle fileHandle = Gdx.files.external(filename);
            JsonValue root = new JsonReader().parse(fileHandle.readString());
            JsonValue jsonScores = root.get("scores");
            int[] scoreAr = jsonScores.asIntArray();
            for (int i : scoreAr) {
                scores.add(i);
            }
        } catch (Exception e) {
        }

    }

    private void write() {
        try {
            FileHandle fileHandle = Gdx.files.external(filename);
            Json json = new Json();
            Writer w = new StringWriter();
            json.setWriter(w);
            json.writeObjectStart();
            json.writeArrayStart("scores");
            for (int score : scores) {
                json.writeValue(score);
            }
            json.writeArrayEnd();
            json.writeObjectEnd();
            fileHandle.writeString(w.toString(), false);
        } catch (Exception e) {
        }
    }

    @Override
    public void show() {
        read();
        Collections.sort(scores, Collections.reverseOrder());
        scores = scores.subList(0, (scores.size() > 10 ? 10 : scores.size()));
        write();
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.isKeyPressed(Input.Keys.ESCAPE)
                || Gdx.input.isKeyPressed(Input.Keys.SPACE)
                || Gdx.input.justTouched()) {
            game.setScreen(new MenuScreen(game));
        }

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        game.batch.begin();
        game.font.setColor(1, 0, 0, 1);
        game.font.draw(game.batch, "Game Over",
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() - LINE_SPACE);
        game.font.draw(game.batch,
                String.format("Your Score: %d", score),
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() - LINE_SPACE * 2);
        game.font.draw(game.batch, "High Scores",
                Gdx.graphics.getWidth() / 2,
                Gdx.graphics.getHeight() - LINE_SPACE * 3);
        int i = 0;
        for (int iscore : scores) {
            game.font.draw(game.batch,
                    String.format("%d", iscore),
                    Gdx.graphics.getWidth() / 2,
                    Gdx.graphics.getHeight() -
                            (++i * LINE_SPACE + (LINE_SPACE * 4)));

        }
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
