package com.xacobe.mario.Sprites;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;

public class NoShurikenDude extends Enemy {
    private float Statetimer;
    private Animation<TextureRegion> attackAnimation;
    Array<TextureRegion> frames;

    public NoShurikenDude(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < 9; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("attack"), (i * 71)-25, 285, 71, 55));
            attackAnimation = new Animation<TextureRegion>(0.1f, frames);
            Statetimer = 0;
            setBounds(getX(), getY(), 71 / MarioBros.PPM, 55 / MarioBros.PPM);

        }
    }

    public void update(float dt) {
        Statetimer += dt;
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y / getHeight() / 2);
        setRegion(attackAnimation.getKeyFrame(Statetimer, true));
        defineEnemy();
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioBros.PPM, 170 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);


        FixtureDef fdef = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10 / MarioBros.PPM, 25 / MarioBros.PPM);//antes era 5
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.ENEMY_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }
}
