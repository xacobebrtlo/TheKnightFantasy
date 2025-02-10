package com.xacobe.mario.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;

public class Personaje extends Sprite {
    public enum State {FALLING, JUMPING, STANDIND, RUNNING, CROUCHING, ATTACKING, ATTACKCROUCH}

    public State currentState;
    public State previusState;
    public World world;
    public Body b2body;
    private Animation<TextureRegion> personajeStatico;
    private Animation<TextureRegion> personajeRun;
    private Animation<TextureRegion> personajeJump;
    private Animation<TextureRegion> personajeFalling;
    private Animation<TextureRegion> personajeCrouching;
    private Animation<TextureRegion> personajeAttacking;
    private Animation<TextureRegion> personajeAttackCrouch;
    public static boolean iscrouching;
    public static boolean isAttacking;
    public static boolean Iscrouching;

    private float stateTimer;
    private boolean runningRight;

    //Mi personaje es de 50*50
    public Personaje(World world, PlayScreen screen) {
        super(screen.getAtlas().findRegion("attack"));
        this.world = world;
        currentState = State.STANDIND;
        previusState = State.STANDIND;
        stateTimer = 0;
        runningRight = true;

        iscrouching = false;
        isAttacking = false;

        //CORREGIR ANIMACION PERSONAJE
        //Correr
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < 8; i++) {
            frames.add(new TextureRegion(getTexture(), 933 + (i * 128), 285, 50, 50));
        }
        personajeRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //SaLtar
        for (int i = 0; i < 3; i++) {
            frames.add(new TextureRegion(getTexture(), i * 120, 228, 70, 50));
        }
        personajeJump = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //Caerse
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(getTexture(), 933 + (i * 128), 57, 50, 50));
        }
        personajeFalling = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //Agacharse
        for (int i = 1; i < 3; i++) {
            frames.add(new TextureRegion(getTexture(), 933 + (i * 128), 0, 50, 50));
        }
        personajeCrouching = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //Atacar
        for (int i = 0; i < 8; i++) {
            frames.add(new TextureRegion(getTexture(), i * 126, 0, 50, 50));
        }
        personajeAttacking = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //Atacar agachado
        for (int i = 0; i < 5; i++) {
            frames.add(new TextureRegion(getTexture(), i * 128, 57, 50, 50));
        }
        personajeAttackCrouch = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //Estático
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 933 + (i * 128), 171, 50, 50));
        }
        personajeStatico = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        definePersonaje();
        setBounds(0, 0, 50 / MarioBros.PPM, 50 / MarioBros.PPM);
        setRegion(personajeStatico.getKeyFrame(stateTimer, true));
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
                isAttacking = false;
                break;
            case RUNNING:
                region = personajeRun.getKeyFrame(stateTimer, true);
                isAttacking = false;
                break;
            case FALLING:
                region = personajeFalling.getKeyFrame(stateTimer, true);
                isAttacking = false;
                break;
            case CROUCHING:
                region = personajeCrouching.getKeyFrame(stateTimer, true);
                isAttacking = false;
                break;
            case ATTACKING:
                region = personajeAttacking.getKeyFrame(stateTimer, false);  // 'false' para que no se repita la animación
                if (personajeAttacking.isAnimationFinished(stateTimer)) {
                    isAttacking = false;            // Cuando termine la animación, desactiva el ataque
                    currentState = State.STANDIND;  // Vuelve al estado STANDIND
                }
                break;
            case ATTACKCROUCH:
                region = personajeAttackCrouch.getKeyFrame(stateTimer);
                break;
            case STANDIND:
            default:
                region = personajeStatico.getKeyFrame(stateTimer, true);
                isAttacking = false;
                break;
        }
        if ((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        } else if ((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }


//        float timer8 = dt+3f;
//        if (currentState == State.ATTACKING && stateTimer < timer8){
//            currentState = State.ATTACKING;
//        }
        if (currentState == previusState) {
            stateTimer += dt;
        } else {
            stateTimer = 0;  // Reinicia el temporizador al cambiar de estado
        }
        previusState = currentState;
        return region;
    }

    public State getState() {

        if (isAttacking && iscrouching) {
            return State.ATTACKCROUCH;
        } else if (isAttacking) {
            return State.ATTACKING;
        } else if (b2body.getLinearVelocity().y < 0) {
            return State.FALLING;
        } else if (b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previusState == State.JUMPING)) {
            return State.JUMPING;
        } else if (b2body.getLinearVelocity().x != 0) {
            return State.RUNNING;
        } else if (iscrouching) {
            return State.CROUCHING;
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
