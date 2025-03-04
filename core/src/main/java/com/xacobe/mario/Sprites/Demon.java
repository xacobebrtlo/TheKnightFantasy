package com.xacobe.mario.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;

public class Demon extends Enemy {
    private float stateTimer;
    private Animation<TextureRegion> attackAnimation;
    private Animation<TextureRegion> idleAnimation;
    public boolean isAttacking;
    private TextureAtlas demonAtlas;
    public boolean setToDestroy;
    public boolean destroyed;
    private Fixture attackFixture;

    private float originalY;
    private float floatAmplitude = 0.5f;  // Movimiento vertical en metros
    private float floatSpeed = 1.5f;      // Velocidad de oscilación

    public Demon(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTimer = 0;
        isAttacking = false;
        setToDestroy = false;
        destroyed = false;
        originalY = y;

        // Cargar el atlas
        demonAtlas = new TextureAtlas(Gdx.files.internal("Demon_and_Health.atlas"));

        // --- Animación de Ataque ---
        TextureRegion attackRegion = demonAtlas.findRegion("demon-attack");
        int frameCountAttack = 11;
        int frameWidthAttack = attackRegion.getRegionWidth() / frameCountAttack;
        int frameHeightAttack = attackRegion.getRegionHeight();
        Array<TextureRegion> attackFrames = new Array<>();
        for (int i = 0; i < frameCountAttack; i++) {
            attackFrames.add(new TextureRegion(attackRegion, i * frameWidthAttack, 0, frameWidthAttack, frameHeightAttack));
        }
        attackAnimation = new Animation<>(0.1f, attackFrames);

        // --- Animación de Idle ---
        TextureRegion idleRegion = demonAtlas.findRegion("demon-idle");
        int frameCountIdle = 6;
        int frameWidthIdle = idleRegion.getRegionWidth() / frameCountIdle;
        int frameHeightIdle = idleRegion.getRegionHeight();
        Array<TextureRegion> idleFrames = new Array<>();
        for (int i = 0; i < frameCountIdle; i++) {
            idleFrames.add(new TextureRegion(idleRegion, i * frameWidthIdle, 0, frameWidthIdle, frameHeightIdle));
        }
        idleAnimation = new Animation<>(0.1f, idleFrames);

        // Ajustamos los bounds del sprite para que se centre correctamente en el cuerpo
        setBounds(getX(), getY(), frameWidthAttack / MarioBros.PPM, frameHeightAttack / MarioBros.PPM);

        // Ataque automático cada 4 segundos
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                if (!destroyed) {
                    ataqueEnemigo();
                }
            }
        }, 4, 4);
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY() + 1); // ⚠️ Asegura que el demonio flote centrado
        bdef.type = BodyDef.BodyType.KinematicBody;
        b2body = world.createBody(bdef);

        // Ajuste de colisión centrada al sprite
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(1f, 0.8f);  // ⚠️ Se adapta al tamaño real del sprite

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1.0f;
        fdef.friction = 0f;
        fdef.filter.categoryBits = MarioBros.DEMON_BIT;
        fdef.filter.maskBits = MarioBros.PERSONAJE_BIT | MarioBros.ATTACK_BIT;  // ⚠️ No choca con el suelo

        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    @Override
    public void update(float dt) {
        stateTimer += dt;

        // Hacer que el demonio flote arriba y abajo
        if (b2body != null) {
            float newY = originalY + floatAmplitude * (float) Math.sin(floatSpeed * stateTimer);
            b2body.setTransform(b2body.getPosition().x, newY, 0);
        }

        // Si está marcado para destruirse, eliminar el cuerpo
        if (setToDestroy && !destroyed) {
            if (b2body != null) {
                world.destroyBody(b2body);
                b2body = null;
            }
            destroyed = true;
        }

        // Si está destruido, ocúltalo
        if (destroyed) {
            setAlpha(0);
            return;
        }

        // Centrar el sprite respecto al body
        if (b2body != null) {
            setPosition(b2body.getPosition().x - getWidth() / 2,
                b2body.getPosition().y - getHeight() / 2);
        }

        // Manejo de animaciones
        if (isAttacking) {
            if (attackAnimation.isAnimationFinished(stateTimer)) {
                removeAttackFixture();
                isAttacking = false;
            }
            setRegion(attackAnimation.getKeyFrame(stateTimer, false));
        } else {
            setRegion(idleAnimation.getKeyFrame(stateTimer, true));
        }
    }

    @Override
    public void hitOnSword() {
        if (!destroyed) {
            setColor(Color.RED);
            setToDestroy = true;
        }
    }

    public void ataqueEnemigo() {
        if (destroyed || setToDestroy || b2body == null) {
            return;
        }

        // Iniciar la animación de ataque
        stateTimer = 0;
        isAttacking = true;

        // Elimina cualquier sensor de ataque previo
        removeAttackFixture();

        FixtureDef fdef = new FixtureDef();
        PolygonShape attack = new PolygonShape();
        attack.set(new Vector2[]{
            new Vector2(-0.5f, -0.25f),
            new Vector2(0.5f, -0.25f),
            new Vector2(0.5f, 0.25f),
            new Vector2(-0.5f, 0.25f)
        });

        fdef.shape = attack;
        fdef.isSensor = true;
        fdef.filter.categoryBits = MarioBros.DEMONATTACK_BIT;

        attackFixture = b2body.createFixture(fdef);
        attackFixture.setUserData("ataqueEnemigo");

        attack.dispose();
    }

    private void removeAttackFixture() {
        if (attackFixture != null) {
            final Fixture fixtureToRemove = attackFixture;
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (fixtureToRemove.getBody() != null) {
                        fixtureToRemove.getBody().destroyFixture(fixtureToRemove);
                    }
                }
            }, 0.1f);
            attackFixture = null;
        }
    }

    @Override
    public void onSwordHit() {
    }

    public void dispose() {
        demonAtlas.dispose();
    }
}
