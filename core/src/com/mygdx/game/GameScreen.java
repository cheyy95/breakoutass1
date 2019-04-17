package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;

/**
 *
 * @author sraven0
 */
public class GameScreen implements Screen {

    // scaling factor for physics
    static final float WORLD_SCALE = 100f;

    // collision bitmasks
    static final short WORLD_BOUND = 1;
    static final short PADDLE = 1 << 1;
    static final short BRICK = 1 << 2;
    static final short BALL = 1 << 3;
    static final short BALL_KILLER = 1 << 4;

    // make sure the ball moves at least this fast
    static final float BALL_SENTINEL = 0.5f;

    // game state
    MyGdxGame game;
    World world;
    Texture padTex;
    Texture ballTex;
    Texture brickTex;
    int brickVPad = 5;
    int brickHPad = 5;
    int areaPad = 150;
    Fixture ballKiller;
    PhysicalSprite paddle;
    PhysicalSprite ball;
    Array<Body> bumpers;
    Array<PhysicalSprite> bricks;
    Array<PhysicalSprite> toBeDestroyed;
    SpriteBatch batch;

    // more game state
    private boolean ballStarted = false;
    private boolean resetBall = false;
    private boolean mouse = false;
    private int score = 0;
    private int scoreMultiplier;
    private int lives = 3;
    private int screenWidth;
    private int screenHeight;



    GameScreen(MyGdxGame game) {
        this.game = game;
        this.score = score;
        this.scoreMultiplier = scoreMultiplier;
        this.lives = lives;
        screenWidth = Gdx.graphics.getWidth();
        screenHeight = Gdx.graphics.getHeight();
        batch = new SpriteBatch();
        padTex = new Texture("paddle128x32.png");
        ballTex = new Texture("ball16x16.png");
        brickTex = new Texture("blue128x64.png");

        bumpers = new Array<Body>();
        bricks = new Array<PhysicalSprite>();
        toBeDestroyed = new Array<PhysicalSprite>();

        // create physics world
        world = new World(new Vector2(0, 0), true);
        world.setContactListener(new ContactListener() {

            @Override
            public void beginContact(Contact contact) {
                Fixture fixa = contact.getFixtureA();
                Fixture fixb = contact.getFixtureB();

                for (PhysicalSprite brick : bricks) {
                    if ((fixa == brick.fixture && fixb == ball.fixture)
                            || (fixa == ball.fixture && fixb == brick.fixture)) {
                        toBeDestroyed.add(brick);
                    }
                }

                if ((fixa == ballKiller && fixb == ball.fixture)
                        || (fixa == ball.fixture && fixb == ballKiller)) {
                    resetBall = true;
                }
            }

            @Override
            public void endContact(Contact contact) {
            }

            @Override
            public void preSolve(Contact contact, Manifold oldManifold) {
            }

            @Override
            public void postSolve(Contact contact, ContactImpulse impulse) {
            }
        });

        createEdges();
        createPaddle();
        createBall();
        createBricks();
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        if (resetBall) {
            //TODO sometimes ball falls/rises slowly after reset
            ball.setScreenPosition(
                    (Gdx.graphics.getWidth() / 2) - (ball.sprite.getWidth() / 2),
                    100,
                    0);
            ball.body.setAngularVelocity(0);
            ball.body.setLinearVelocity(0, 0);
            ball.body.setAwake(false);// TODO did this fix it?
            resetBall = false;
            ballStarted = false;
            if (lives-- == 0) {
                game.setScreen(new GameOverScreen(game, score));
                dispose();
                return;
            }
        }

        // HANDLE INPUT
        if (Gdx.input.isTouched()) {
            mouse = true;
        }
        if (Gdx.input.getDeltaX() != 0) {
            mouse = true;
        }
        if (Gdx.input.isKeyJustPressed(Keys.ESCAPE)) {
            game.setScreen(new PausedScreen(game, this));
        }

        if (Gdx.input.isKeyPressed(Keys.LEFT)) {
            paddle.body.applyForceToCenter(-1f, 0f, true);
            mouse = false;
        } else if (Gdx.input.isKeyPressed(Keys.RIGHT)) {
            paddle.body.applyForceToCenter(1f, 0f, true);
            mouse = false;
        } else if (mouse) {
            float mousex = Gdx.input.getX() / WORLD_SCALE;
            float padx = paddle.body.getPosition().x;
            paddle.body.applyForceToCenter(mousex - padx, 0f, true);
        }

        // push the ball when user presses space or clicks
        if (!ballStarted
                && (Gdx.input.isKeyJustPressed(Keys.SPACE)
                || Gdx.input.justTouched())) {
            ball.body.applyForceToCenter(
                    (float) (((Math.random() * 2) - 1) / 10),
                    (float) (((Math.random() * 2) - 1) / 10),
                    true);
            ball.body.applyAngularImpulse(
                    (float) (((Math.random() * 2) - 1) / 1000),
                    true);
            ballStarted = true;
        }

        // UPDATE PHYSICS
        world.step(delta, 6, 2);

        // destroy any collided bricks
        while (toBeDestroyed.size != 0) {
            PhysicalSprite dead = toBeDestroyed.first();
            toBeDestroyed.removeIndex(0);
            world.destroyBody(dead.body);
            if (bricks.contains(dead, true)) {
                bricks.removeValue(dead, true);
                score += scoreMultiplier;
            }
        }

        // level up


        // ensure the ball has a minimum velocity
        if (ballStarted) {
            Vector2 ballVel = ball.body.getLinearVelocity();
            if (Math.abs(ballVel.x) < BALL_SENTINEL) {
                if (ballVel.x == 0f) {
                    ball.body.applyForceToCenter(BALL_SENTINEL, 0, true);
                } else {
                    ball.body.applyForceToCenter(
                            BALL_SENTINEL * Math.signum(ballVel.x), 0, true);
                }
            }
            if (Math.abs(ballVel.y) < BALL_SENTINEL) {
                if (ballVel.y == 0f) {
                    ball.body.applyForceToCenter(
                            0, BALL_SENTINEL, true);
                } else {
                    ball.body.applyForceToCenter(
                            0, BALL_SENTINEL * Math.signum(ballVel.y), true);
                }
            }
            if (ball.body.getAngularVelocity() == 0.0f) {
                ball.body.applyAngularImpulse(
                        (float) (((Math.random() * 2) - 1) / 1000),
                        true);
            }
        }

        // UPDATE SPRITE POSITIONS FROM PHYSICS DATA
        paddle.update();
        ball.update();
        for (PhysicalSprite brick : bricks) {
            brick.update();
        }

        // DRAW SPRITES
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        batch.draw(padTex,(screenWidth / 2) - (padTex.getWidth() / 2),3);
        batch.draw(ballTex,(screenWidth / 2),100);
        for (PhysicalSprite brick : bricks) {
            brick.draw(batch);
        }
        //drawScore();
        //drawDebug();
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        screenWidth = width;
        screenHeight = height;
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
        // make sure we garbage collect native resources
        padTex.dispose();
        ballTex.dispose();
        brickTex.dispose();
        world.dispose();
    }

    private void drawDebug() {
        // draw some debugging info
        game.font.setColor(1, 0, 0, 1);
        game.font.draw(game.batch,
                String.format("Paddle: %f, %f %s",
                        paddle.sprite.getX(),
                        paddle.sprite.getY(),
                        paddle.body.getPosition()),
                0, 50);
        game.font.draw(game.batch,
                String.format("Ball: (%f, %f), %f, %f",
                        ball.body.getLinearVelocity().x,
                        ball.body.getLinearVelocity().y,
                        ball.body.getAngularVelocity(),
                        ball.body.getInertia()),
                0, 100);

    }

    /*private void drawScore() {
        game.font.setColor(1, 0, 0, 1);
        game.font.draw(game.batch, String.format("Score: %d", score), 0, 20);
        game.batch.draw(ballTex, Gdx.graphics.getWidth() - ballTex.getWidth() - 110, 5);
        game.font.draw(game.batch, String.format("x %d", lives), Gdx.graphics.getWidth() - 100, 20);
    }*/

    private void createPaddle() {
        float x = (screenWidth / 2) - (padTex.getWidth() / 2);
        float y = 3;
        PhysicalSprite.Defs defs = PhysicalSprite.Defs.fromScreenCoordinates(
                padTex, x, y, WORLD_SCALE);

        defs.bodyDef.type = BodyType.DynamicBody;
        defs.bodyDef.fixedRotation = true;

        defs.fixtureDef.density = 0.1f;
        defs.fixtureDef.filter.categoryBits = PADDLE;
        defs.fixtureDef.filter.maskBits = (BALL | WORLD_BOUND);

        paddle = new PhysicalSprite(defs, world);
        paddle.body.setLinearDamping(2.0f);
    }

    private void createBall() {
        float x = screenWidth / 2;
        float y = 100;

        PhysicalSprite.Defs defs = PhysicalSprite.Defs.fromScreenCoordinates(
                ballTex, x, y, WORLD_SCALE);

        defs.bodyDef.type = BodyType.DynamicBody;

        defs.fixtureDef.friction = 0;
        defs.fixtureDef.density = 0.1f;
        defs.fixtureDef.restitution = 1f;
        defs.fixtureDef.filter.categoryBits = BALL;
        defs.fixtureDef.filter.maskBits = (PADDLE | BRICK | WORLD_BOUND | BALL_KILLER);

        ball = new PhysicalSprite(defs, world);
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
                Gdx.app.log("Creating", String.format("at row %d and col %d", row, col));

                PhysicalSprite.Defs defs = PhysicalSprite.Defs.fromScreenCoordinates(
                        brickTex, col, row, WORLD_SCALE);

                defs.bodyDef.type = BodyType.DynamicBody;

                defs.fixtureDef.density = 0.1f;
                defs.fixtureDef.filter.categoryBits = BRICK;
                defs.fixtureDef.filter.maskBits = (BALL | WORLD_BOUND);

                PhysicalSprite brick = new PhysicalSprite(defs, world);

                bricks.add(brick);
            }
        }
    }

    private void createEdges() {
        float w = Gdx.graphics.getWidth() / WORLD_SCALE;
        float h = Gdx.graphics.getHeight() / WORLD_SCALE;

        //left:  0, 0, 0, h
        //right: w, 0, w, h
        //bottom:0, 0, w, 0
        //top:   0, h, w, h
        for (Vector2[] points : new Vector2[][]{
                {new Vector2(0, 0), new Vector2(0, h)}, // left
                {new Vector2(w, 0), new Vector2(w, h)}, // right
                {new Vector2(0, 0), new Vector2(w, 0)}, // top
                {new Vector2(0, h), new Vector2(w, h)}} // bottom
        ) {
            Vector2 p1, p2;
            p1 = points[0];
            p2 = points[1];
            Gdx.app.log("GameScreen", String.format("Creating edge from %s to %s", p1, p2));

            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(0f, 0f);

            EdgeShape shape = new EdgeShape();
            shape.set(p1, p2);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = WORLD_BOUND;

            Body body = world.createBody(bodyDef);
            body.createFixture(fixtureDef);

            bumpers.add(body);
            shape.dispose();
        }

        // create ball killer
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0f, 0f);

        EdgeShape shape = new EdgeShape();
        shape.set(0, 1 / WORLD_SCALE, w, 1 / WORLD_SCALE);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = BALL_KILLER;

        Body body = world.createBody(bodyDef);
        ballKiller = body.createFixture(fixtureDef);

        bumpers.add(body);
        shape.dispose();
    }

}