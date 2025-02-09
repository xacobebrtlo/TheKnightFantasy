package com.xacobe.mario.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;

public class Personaje extends Sprite {
    public enum State {FALLING, JUMPING, STANDIND, RUNNING};
    public State currentState;
    public State previusState;
    public World world;
    public Body b2body;
    private TextureRegion personajeStatico;
    private Animation<TextureRegion> personajeRun;
    private Animation<TextureRegion> personajeJump;

    private float stateTimer;
    private boolean runningRight;

    //Mi personaje es de 32*32
    public Personaje(World world, PlayScreen screen) {
        super(screen.getAtlas().findRegion("idle"));
        this.world = world;
        currentState = State.STANDIND;
        previusState = State.STANDIND;
        stateTimer = 0;
        runningRight = true;

        //CORREGIR ANIMACION PERSONAJE
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 8; i++) {
            frames.add(new TextureRegion(getTexture(), 933+(i *64), 285, 64, 44));
        }
        personajeRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();
        for (int i = 8; i < 11; i++) {
                frames.add(new TextureRegion(getTexture(), (i * 101), 228, 101, 46));
        }
        personajeJump = new Animation<TextureRegion>(0.3f, frames);

        personajeStatico = new TextureRegion(getTexture(), 933, 171, 44, 44);
        definePersonaje();
        setBounds(0, 0, 44 / MarioBros.PPM, 44 / MarioBros.PPM);
        setRegion(personajeStatico);
    }

    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));
    }

    public TextureRegion getFrame(float dt) {
        currentState = getState();
        TextureRegion region;
        switch (currentState) {
            case JUMPING:
                region = personajeJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = personajeRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
                //completar FALLING
            case STANDIND:
            default:
                region = personajeStatico;
                break;
        }
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previusState ? stateTimer + dt : 0;
        previusState=currentState;
        return region;
    }

    public State getState() {
        if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previusState == State.JUMPING)) {
            return State.JUMPING;
        } else if (b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else {
            return State.STANDIND;
        }
    }

    public void definePersonaje() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MarioBros.PPM, 170 / MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(15 / MarioBros.PPM);//antes era 5

        fdef.shape = shape;
        b2body.createFixture(fdef);
    }
}
