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

    public Demon(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTimer = 0;
        isAttacking = false;
        setToDestroy = false;
        destroyed = false;

        // Cargar el atlas
        demonAtlas = new TextureAtlas(Gdx.files.internal("Demon_and_Health.atlas"));

        // --- Animación de Ataque ---
        TextureRegion attackRegion = demonAtlas.findRegion("demon-attack");
        if (attackRegion == null) {
            Gdx.app.error("Demon", "No se encontró la región 'demon-attack'");
        }
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
        if (idleRegion == null) {
            Gdx.app.error("Demon", "No se encontró la región 'demon-idle'");
        }
        int frameCountIdle = 6;
        int frameWidthIdle = idleRegion.getRegionWidth() / frameCountIdle;
        int frameHeightIdle = idleRegion.getRegionHeight();
        Array<TextureRegion> idleFrames = new Array<>();
        for (int i = 0; i < frameCountIdle; i++) {
            idleFrames.add(new TextureRegion(idleRegion, i * frameWidthIdle, 0, frameWidthIdle, frameHeightIdle));
        }
        idleAnimation = new Animation<>(0.1f, idleFrames);

        // Establecer los bounds (más pequeño que antes)
        setBounds(getX(), getY(), 120 / MarioBros.PPM, 96 / MarioBros.PPM);

        // Ataque automático cada 3 segundos
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                ataqueEnemigo();
            }
        }, 3, 3);
    }

    @Override
    protected void defineEnemy() {
        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        // Ajustar la colisión al sprite (más pequeña que antes)
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((60 / 2.5f) / MarioBros.PPM, (80 / 3f) / MarioBros.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1.0f;
        fdef.friction = 0.4f;
        fdef.filter.categoryBits = MarioBros.DEMON_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.PERSONAJE_BIT | MarioBros.ATTACK_BIT;

        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    @Override
    public void update(float dt) {
        stateTimer += dt;

        // Eliminar el Demon cuando sea destruido
        if (setToDestroy && !destroyed && b2body.getLinearVelocity().x == 0) {
            world.destroyBody(b2body);

            b2body = null;
            destroyed = true;
        }

        if (b2body != null) {
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
        }

        // Si no está destruido, gestionar animaciones
        if (!destroyed) {
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
    }

    public void hitOnSword() {
        // Cambia de color al ser golpeado
        setColor(Color.RED);

        // Agrega impulso al cuerpo
        if (b2body != null && b2body.getLinearVelocity().y == 0 && b2body.getLinearVelocity().x == 0) {
            b2body.applyLinearImpulse(new Vector2(0.5f, 1f), b2body.getWorldCenter(), true);
        }

        // Marca para destruir
        setToDestroy = true;
    }

    public void ataqueEnemigo() {
        if (destroyed || setToDestroy || b2body == null) {
            return;
        }

        // Iniciar animación de ataque
        stateTimer = 0;
        isAttacking = true;

        // Si ya existe un sensor de ataque, lo eliminamos primero
        removeAttackFixture();

        FixtureDef fdef = new FixtureDef();
        PolygonShape attack = new PolygonShape();
        attack.set(new Vector2[]{
            new Vector2(0 / MarioBros.PPM, -10 / MarioBros.PPM),
            new Vector2(-30 / MarioBros.PPM, -5 / MarioBros.PPM),
            new Vector2(-30 / MarioBros.PPM, 10 / MarioBros.PPM),
            new Vector2(0 / MarioBros.PPM, 20 / MarioBros.PPM)
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
    public void onSwordHit() {}

    public void dispose() {
        demonAtlas.dispose();
    }
}
