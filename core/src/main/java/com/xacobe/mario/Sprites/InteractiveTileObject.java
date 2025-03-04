package com.xacobe.mario.Sprites;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;

import java.awt.Rectangle;

public abstract class InteractiveTileObject {
    protected Rectangle bounds;
    protected Body body;
    protected Fixture fixture;
    public World world;
    public TiledMap map;

    public InteractiveTileObject(PlayScreen screen, Rectangle bounds) {
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = bounds;

        BodyDef bdef = new BodyDef();
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((float) ((bounds.getX() + bounds.getWidth() / 2) / MarioBros.PPM), (float) ((bounds.getY() + bounds.getHeight() / 2) / MarioBros.PPM));
        body = world.createBody(bdef);

        shape.setAsBox((float) (bounds.getWidth() / 2 / MarioBros.PPM), (float) (bounds.getHeight() / 2 / MarioBros.PPM));
        fdef.shape = shape;
        fixture = body.createFixture(fdef);

    }

    public abstract void onTouch();
}
