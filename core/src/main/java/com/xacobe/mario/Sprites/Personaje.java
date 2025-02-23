package com.xacobe.mario.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;

public class Personaje extends Sprite {
    public enum State {FALLING, JUMPING, STANDIND, RUNNING, CROUCHING, ATTACKING, ATTACKCROUCH, JUMPATTACK}

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
    private Animation<TextureRegion> personajeJumpAttack;
    public static boolean iscrouching;
    public static boolean isAttacking;
    public static boolean isJumpAttack;


    public static float stateTimer;
    public boolean runningRight;

    //Mi personaje es de 50*50
    public Personaje(PlayScreen screen) {
        super(screen.getAtlas().findRegion("attack"));
        this.world = screen.getWorld();
        currentState = State.STANDIND;
        previusState = State.STANDIND;
        stateTimer = 0;
        runningRight = true;

        iscrouching = false;
        isAttacking = false;

        //TODO corregir palo salto
        //Correr
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < 8; i++) {
            frames.add(new TextureRegion(getTexture(), 933 + (i * 128) - 41, 285, 116, 44));
        }
        personajeRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //SaLtar
        for (int i = 0; i < 3; i++) {
            frames.add(new TextureRegion(getTexture(), (i * 128) - 20, 228, 101, 46));
        }
        personajeJump = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //Caerse
        for (int i = 0; i < 2; i++) {
            frames.add(new TextureRegion(getTexture(), 933 + (i * 128) - 20, 57 - 5, 84, 50));
        }
        personajeFalling = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //Agacharse
        for (int i = 1; i < 3; i++) {
            frames.add(new TextureRegion(getTexture(), 933 + (i * 128) - 47, 0, 94, 44));
        }
        personajeCrouching = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //Atacar
        for (int i = 1; i < 7; i++) {
            frames.add(new TextureRegion(getTexture(), (i * 128) - 45, 0, 113, 48));
        }
        personajeAttacking = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //Atacar agachado
        for (int i = 1; i < 5; i++) {
            frames.add(new TextureRegion(getTexture(), (i * 128) - 46, 52, 107, 37));
        }
        personajeAttackCrouch = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        //Atacar saltando
        for (int i = 1; i < 5; i++) {
            frames.add(new TextureRegion(getTexture(), 933 + (i * 128) - 38, 228 - 5, 110, 50));
        }
        personajeJumpAttack = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();
        //Estático
        for (int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(getTexture(), 933 + (i * 128) - 39, 171, 102, 44));
        }
        personajeStatico = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        definePersonaje();
        setBounds(0, 0, 113 / MarioBros.PPM, 50 / MarioBros.PPM);
        setRegion(personajeStatico.getKeyFrame(stateTimer, true));
    }


    public void update(float dt) {
        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        setRegion(getFrame(dt));

        if (currentState == State.ATTACKING) {
            if (personajeAttacking.isAnimationFinished(stateTimer)) {
                isAttacking = false;
                currentState = State.STANDIND;
                removeAttackFixture();
            }
        } else if (currentState == State.ATTACKCROUCH) {
            if (personajeAttackCrouch.isAnimationFinished(stateTimer)) {
                isAttacking = false;
                currentState = State.CROUCHING;
                removeAttackFixture();
            }
        } else if (currentState == State.JUMPATTACK) {
            if (personajeJumpAttack.isAnimationFinished(stateTimer)) {
                isJumpAttack = false;
                currentState = State.FALLING;
                removeAttackFixture();
            }
        }
    }

    // Nuevo método para eliminar la fixture de ataque de forma segura
    private void removeAttackFixture() {
        if (attackFixture != null) {
            b2body.destroyFixture(attackFixture);
            attackFixture = null;  // Para evitar intentos de destrucción múltiples
        }
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
//                isAttacking = false;
                break;
            case ATTACKING:
                region = personajeAttacking.getKeyFrame(stateTimer, false);  // 'false' para que no se repita la animación

//                if (personajeAttacking.isAnimationFinished(stateTimer)) {
//                    isAttacking = false;            // Cuando termine la animación, desactiva el ataque
//                    currentState = State.STANDIND;  // Vuelve al estado STANDIND
//                    world.destroyBody(bodyAttack);
//                }
                break;
            case ATTACKCROUCH:
                region = personajeAttackCrouch.getKeyFrame(stateTimer);
//                if (personajeAttackCrouch.isAnimationFinished(stateTimer)) {
//                    isAttacking = false;            // Cuando termine la animación, desactiva el ataque
//                    currentState = State.CROUCHING;  // Vuelve al estado STANDIND
//                    world.destroyBody(bodyAttack);
//                }
                break;
            case JUMPATTACK:
                region = personajeJumpAttack.getKeyFrame(stateTimer);

//                if (personajeJumpAttack.isAnimationFinished(stateTimer)) {
//                    isJumpAttack = false;
//                    currentState = State.FALLING;
//                    world.destroyBody(bodyAttack);
//                }

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


        stateTimer = currentState == previusState ? stateTimer + dt : 0;

        previusState = currentState;
        return region;
    }

    public State getState() {

        if (isJumpAttack) {
            return State.JUMPATTACK;
        } else if (isAttacking && iscrouching) {
            return State.ATTACKCROUCH;
        } else if (isAttacking && b2body.getLinearVelocity().y == 0) {
            return State.ATTACKING;
        } else if (b2body.getLinearVelocity().y < 0 && currentState != State.JUMPATTACK) {
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

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(10 / MarioBros.PPM, 25 / MarioBros.PPM);//antes era 5
        fdef.filter.categoryBits = MarioBros.PERSONAJE_BIT;
        fdef.filter.maskBits=MarioBros.GROUND_BIT | MarioBros.ENEMY_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef);


    }

    private Fixture attackFixture; // Guardar la referencia de la fixture

    public void hitBoxAtaque() {
        if (attackFixture != null) {
            b2body.destroyFixture(attackFixture); // Eliminar si ya existe
            attackFixture = null;
        }

        FixtureDef fdef = new FixtureDef();
        PolygonShape attack = new PolygonShape();

        if (isAttacking || isJumpAttack || currentState == State.ATTACKCROUCH) {
            if (runningRight) {

                attack.set(new Vector2[]{
                    new Vector2(0 / MarioBros.PPM, -15 / MarioBros.PPM),
                    new Vector2(56 / MarioBros.PPM, -5 / MarioBros.PPM),
                    new Vector2(45 / MarioBros.PPM, 10 / MarioBros.PPM),
                    new Vector2(0 / MarioBros.PPM, 23 / MarioBros.PPM)
                });
            } else {
                attack.set(new Vector2[]{
                    new Vector2(0 / MarioBros.PPM, -15 / MarioBros.PPM),
                    new Vector2(-56 / MarioBros.PPM, -5 / MarioBros.PPM),
                    new Vector2(-45 / MarioBros.PPM, 10 / MarioBros.PPM),
                    new Vector2(0 / MarioBros.PPM, 23 / MarioBros.PPM)
                });
            }

            fdef.shape = attack;
            fdef.isSensor = true;
            fdef.filter.categoryBits=MarioBros.ATTACK_BIT;
            attackFixture = b2body.createFixture(fdef); // Guardamos la fixture
            attackFixture.setUserData("sword");
        }
    }


}
