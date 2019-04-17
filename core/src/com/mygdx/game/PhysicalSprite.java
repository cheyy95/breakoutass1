package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

/**
 *
 * @author sraven0
 */
public class PhysicalSprite {

    Sprite sprite;
    Body body;
    BodyDef bodyDef;
    FixtureDef fixtureDef;
    Fixture fixture;
    private float scale;

    public PhysicalSprite(Texture tex, World world, float x, float y, float scale, BodyDef bodyDef, FixtureDef fixtureDef, Object bodyUserData, Object fixtureUserData) {
        this.scale = scale;
        sprite = new Sprite(tex);
        sprite.setPosition(x, y);

        this.bodyDef = bodyDef;
        body = world.createBody(bodyDef);
        body.setUserData(bodyUserData);

        this.fixtureDef = fixtureDef;
        fixture = body.createFixture(fixtureDef);
        fixture.setUserData(fixtureUserData);
    }

    public PhysicalSprite(Texture tex, World world, float x, float y, float scale, BodyDef bodyDef, FixtureDef fixtureDef) {
        this(tex, world, x, y, scale, bodyDef, fixtureDef, null, null);
    }

    public PhysicalSprite(Defs defs, World world, Object bodyUserData, Object fixtureUserData) {
        this(defs.tex, world, defs.x, defs.y, defs.scale, defs.bodyDef, defs.fixtureDef, bodyUserData, fixtureUserData);
    }

    public PhysicalSprite(Defs defs, World world) {
        this(defs, world, null, null);
    }

    /**
     * Sets this sprite's position, in screen coordinates (pixels).
     *
     * @param x
     * @param y
     * @param angle
     */
    public void setScreenPosition(float x, float y, float angle) {
        sprite.setX(x);
        sprite.setY(y);
        body.setTransform(new Vector2(
                        (x + sprite.getWidth() / 2) / scale,
                        (y + sprite.getHeight() / 2) / scale),
                angle);
    }

    /**
     * Sets this sprite's position, in physics world coordinates (meters).
     *
     * @param x
     * @param y
     * @param angle
     */
    public void setWorldPosition(float x, float y, float angle) {
        body.setTransform(x, y, angle);
        update();
    }

    /**
     * update sprite's position from physics body's position
     */
    public void update() {
        sprite.setPosition(
                (body.getPosition().x * scale) - sprite.getWidth() / 2,
                (body.getPosition().y * scale) - sprite.getHeight() / 2);
        sprite.setRotation((float) Math.toDegrees(body.getAngle()));
    }

    /**
     * Draws this sprite onto the given sprite batch.
     *
     * @param batch
     */
    public void draw(SpriteBatch batch) {
        batch.draw(sprite,
                sprite.getX(),
                sprite.getY(),
                sprite.getOriginX(),
                sprite.getOriginY(),
                sprite.getWidth(),
                sprite.getHeight(),
                sprite.getScaleX(),
                sprite.getScaleY(),
                sprite.getRotation());
    }

    /**
     * helper for initializing BodyDef and FixtureDef to a given size and
     * position
     */
    public static class Defs {

        public Texture tex;
        public BodyDef bodyDef;
        public FixtureDef fixtureDef;
        float x;
        float y;
        float scale;

        private Defs() {
        }

        /**
         * Creates BodyDef and FixtureDef from given screen coordinates.
         *
         * @param tex texture whose size is used as the size of the created
         * FixtureDef
         * @param x x-location of left edge of texture, in screen coordinates
         * @param y y-location of bottom edge of texture, in screen coordinates
         * @param scale scaling factor for conversion between world coordinates
         * and screen coordinates
         * @return
         */
        public static Defs fromScreenCoordinates(Texture tex, float x, float y, float scale) {
            Defs defs = new Defs();
            defs.tex = tex;
            defs.x = x;
            defs.y = y;
            defs.scale = scale;

            defs.bodyDef = new BodyDef();
            defs.bodyDef.position.set(
                    (x + tex.getWidth() / 2) / scale,
                    (y + tex.getHeight() / 2) / scale);

            defs.fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(
                    tex.getWidth() / 2 / scale,
                    tex.getHeight() / 2 / scale);
            defs.fixtureDef.shape = shape;
            return defs;
        }

        /**
         * Creates BodyDef and FixtureDef from given world coordinates.
         *
         * @param tex texture whose size is used as the size of the created
         * FixtureDef
         * @param x x-location of center of physics body, in world coordinates
         * @param y y-location of center of physics body, in world coordinates
         * @param scale scaling factor for conversion between world coordinates
         * and screen coordinates
         * @return
         */
        public static Defs fromWorldCoordinates(Texture tex, float x, float y, float scale) {
            Defs defs = new Defs();
            defs.tex = tex;
            defs.x = x * scale;
            defs.y = y * scale;
            defs.scale = scale;

            defs.bodyDef = new BodyDef();
            defs.bodyDef.position.set(x, y);

            defs.fixtureDef = new FixtureDef();
            PolygonShape shape = new PolygonShape();
            shape.setAsBox(
                    tex.getWidth() / 2 / scale,
                    tex.getHeight() / 2 / scale);
            defs.fixtureDef.shape = shape;
            return defs;
        }
    }

}
