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
   public boolean isAttacking = false;
    private Fixture fixture;

    public NoShurikenDude(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 0; i < 9; i++) {
            frames.add(new TextureRegion(screen.getAtlas().findRegion("attack"), (i * 80) - 25, 285, 71, 55));
        }
            attackAnimation = new Animation<TextureRegion>(0.1f, frames);
            Statetimer = 0;
            setBounds(getX(), getY(), 74 / MarioBros.PPM, 55 / MarioBros.PPM);

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
            b2body = null;  // Evitar referencias a un body destruido
            destroyed = true;
        }

        if (b2body != null) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }

        //TODO corregir animacion enemigo
        if (!destroyed) {
            if (isAttacking) {
//                setRegion(attackAnimation.getKeyFrame(Statetimer, false));
                if (attackAnimation.isAnimationFinished(Statetimer)) {
                    // La animación ha finalizado:
                    removeAttackFixture();   // Elimina el sensor de ataque
                    isAttacking = false;       // Se desactiva el estado de ataque
                    setRegion(attackAnimation.getKeyFrame(0.1f));  // Congela en el frame 0
                } else {
                    setRegion(attackAnimation.getKeyFrame(Statetimer, false));
                }
            } else {
                // Si no está atacando, mostrar la animación estática (frame 0)
                setRegion(Staticanimation.getKeyFrame(0.1f));
            }
        }

    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());// TODO  tengo que poner eso, si lo pongo se cae  32 / MarioBros.PPM, 170 / MarioBros.PPM
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);


        FixtureDef fdef = new FixtureDef();

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(13 / MarioBros.PPM, 25 / MarioBros.PPM);//antes era 5
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.ENEMY_BIT | MarioBros.ATTACK_BIT|MarioBros.PERSONAJE_BIT;

        fdef.shape = shape;

        b2body.createFixture(fdef).setUserData(this);

        //Crear cuerpo

        fixture = b2body.createFixture(fdef);
    }

    public void hitOnSword() {
        // Cambia el color para indicar que fue golpeado
        setColor(new Color(Color.RED));

        if (b2body.getLinearVelocity().y == 0 && b2body.getLinearVelocity().x == 0) {
            b2body.applyLinearImpulse(new Vector2(0.5f, 0.8f), b2body.getWorldCenter(), true);
        }

//        // Cancela la tarea programada para el ataque si existe
//        if (attackTask != null) {
//            attackTask.cancel();
//        }

        removeAttackFixture();
        setToDestroy = true;
    }


    private Fixture attackFixture;

    public void ataqueEnemigo() {
        // Evitamos ejecutar si el enemigo ya ha sido destruido o marcado para destruir
        if (destroyed || setToDestroy || b2body == null) {
            return;
        }

        // Reiniciamos el temporizador de la animación para iniciar desde el frame 0
        Statetimer = 0;

        // Si ya existe un sensor de ataque, lo eliminamos primero
        if (attackFixture != null) {
            b2body.destroyFixture(attackFixture);
            attackFixture = null;
        }

        FixtureDef fdef = new FixtureDef();
        PolygonShape attack = new PolygonShape();
        attack.set(new Vector2[]{
            new Vector2(0 / MarioBros.PPM, -15 / MarioBros.PPM),
            new Vector2(-27 / MarioBros.PPM, -5 / MarioBros.PPM),
            new Vector2(-27 / MarioBros.PPM, 10 / MarioBros.PPM),
            new Vector2(0 / MarioBros.PPM, 23 / MarioBros.PPM)
        });

        fdef.shape = attack;
        fdef.isSensor = true;
        fdef.filter.categoryBits = MarioBros.ENEMYATTACK_BIT;
        attackFixture = b2body.createFixture(fdef);
        attackFixture.setUserData("ataqueEnemigo");

        attack.dispose();  // Liberar el recurso del shape
    }


    private void removeAttackFixture() {
        if (attackFixture != null) {
            b2body.destroyFixture(attackFixture);
            attackFixture = null;  // Para evitar múltiples destrucciones
        }
    }


    @Override
    public void onSwordHit() {

    }
}
