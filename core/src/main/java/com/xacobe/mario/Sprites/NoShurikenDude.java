package com.xacobe.mario.Sprites;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;

import java.awt.Rectangle;
import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.xacobe.mario.Tools.B2WorldCreator;

public class NoShurikenDude extends Enemy {
    float deltatimer;
    private float Statetimer;
    private Animation<TextureRegion> attackAnimation;
    Animation<TextureRegion> Staticanimation;
    private boolean setToDestroy;
    public boolean destroyed;
    private boolean estatico;
    private Fixture fixture;

    public NoShurikenDude(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < 9; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("attack"), (i * 80) - 25, 285, 71, 55));
            attackAnimation = new Animation<TextureRegion>(0.1f, frames);
            Statetimer = 0;
            setBounds(getX(), getY(), 71 / MarioBros.PPM, 58 / MarioBros.PPM);

        }
        Staticanimation = new Animation<TextureRegion>(0.1f, attackAnimation.getKeyFrame(0));

        setToDestroy = false;
        destroyed = false;
        deltatimer = 0f;
    }

    public void update(float dt) {
        Statetimer += dt;
        //Destruyo el body del enemigo y su imagen
        if (setToDestroy && !destroyed && b2body.getLinearVelocity().x == 0) {
            world.destroyBody(b2body);
//            b2body.destroyFixture(fixture);
            destroyed = true;
            setRegion(attackAnimation.getKeyFrame(0));
        }

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y / getHeight() / 2);

        //TODO corregir animacion enemigo
        if (!destroyed) {

            setRegion(attackAnimation.getKeyFrame(Statetimer, true));
        }
//        if (attackAnimation.isAnimationFinished(Statetimer)) {
////                estatico = true;
//            setRegion(Staticanimation.getKeyFrame(Statetimer));
////                setRegion(attackAnimation.getKeyFrame(Statetimer));
////            attackAnimation.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);
//        }


//        if (estatico) {
//            setRegion(Staticanimation.getKeyFrame(Statetimer, true));
//            estatico = false;
//        }


    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());// TODO  tengo que poner eso, si lo pongo se cae  32 / MarioBros.PPM, 170 / MarioBros.PPM
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);


        FixtureDef fdef = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10 / MarioBros.PPM, 25 / MarioBros.PPM);//antes era 5
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.ENEMY_BIT | MarioBros.ATTACK_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        //Crear cuerpo
        Rectangle cuerpo = new Rectangle();
        fixture = b2body.createFixture(fdef);
    }

    @Override
    public void hitOnSword() {
        //Poner enemigo en rojo una vez es golpeado
        setColor(new Color(Color.RED));
        if (b2body.getLinearVelocity().y == 0 && b2body.getLinearVelocity().x == 0) {
            b2body.applyLinearImpulse(new Vector2(0.5f, 0.8f), b2body.getWorldCenter(), true);

        }

        setToDestroy = true;
    }

    @Override
    public void onSwordHit() {

    }
}
