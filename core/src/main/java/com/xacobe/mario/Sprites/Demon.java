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

    // Usamos las dimensiones de la región "demon-attack" (2640x192 para 11 frames → cada frame 240x192)
    // Para que ambas animaciones se muestren del mismo tamaño, fijamos el sprite a 240x192 (en píxeles) convertido a metros.
    private final float spriteWidth = 240 / MarioBros.PPM;
    private final float spriteHeight = 192 / MarioBros.PPM;

    public Demon(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        stateTimer = 0;
        isAttacking = false;
        setToDestroy = false;
        destroyed = false;

        // Cargar el atlas específico para Demon
        demonAtlas = new TextureAtlas(Gdx.files.internal("Demon_and_Health.atlas"));

        // --- Animación de Ataque ---
        TextureRegion attackRegion = demonAtlas.findRegion("demon-attack");
        if (attackRegion == null) {
            Gdx.app.error("Demon", "No se encontró la región 'demon-attack'");
        }
        int frameCountAttack = 11;
        int frameWidthAttack = attackRegion.getRegionWidth() / frameCountAttack; // Debe ser 240
        int frameHeightAttack = attackRegion.getRegionHeight();                  // Debe ser 192
        Array<TextureRegion> attackFrames = new Array<TextureRegion>();
        for (int i = 0; i < frameCountAttack; i++) {
            attackFrames.add(new TextureRegion(attackRegion, i * frameWidthAttack, 0, frameWidthAttack, frameHeightAttack));
        }
        attackAnimation = new Animation<TextureRegion>(0.1f, attackFrames);

        // --- Animación de Idle ---
        TextureRegion idleRegion = demonAtlas.findRegion("demon-idle");
        if (idleRegion == null) {
            Gdx.app.error("Demon", "No se encontró la región 'demon-idle'");
        }
        int frameCountIdle = 6;
        int frameWidthIdle = idleRegion.getRegionWidth() / frameCountIdle; // 960/6 = 160
        int frameHeightIdle = idleRegion.getRegionHeight();                // 144
        Array<TextureRegion> idleFrames = new Array<TextureRegion>();
        for (int i = 0; i < frameCountIdle; i++) {
            idleFrames.add(new TextureRegion(idleRegion, i * frameWidthIdle, 0, frameWidthIdle, frameHeightIdle));
        }
        // Nota: aunque los frames idle son 160x144, al fijar los bounds del sprite (240x192)
        // se estirarán para mantener un tamaño uniforme.
        idleAnimation = new Animation<TextureRegion>(0.1f, idleFrames);

        // Establecer los bounds del sprite para que tenga el tamaño deseado
        setBounds(getX(), getY(), spriteWidth, spriteHeight);

        // Programa el ataque automático cada 4 segundos
        Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                // Solo inicia el ataque si el demonio aún no ha sido destruido
                if (!destroyed) {
                    ataqueEnemigo();
                }
            }
        }, 4, 4);
    }

    @Override
    protected void defineEnemy() {
        // Usamos un cuerpo kinemático para que el demonio "flote" (no se vea afectado por la gravedad)
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.KinematicBody;
        // Colocamos el cuerpo centrado en el sprite
        bdef.position.set(getX() + spriteWidth / 2, getY() + spriteHeight / 3);
        b2body = world.createBody(bdef);

        // Crear una forma de colisión centrada en el sprite, con las mismas dimensiones que el sprite
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(spriteWidth / 4, spriteHeight / 4);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.density = 1.0f;
        fdef.friction = 0f;
        fdef.filter.categoryBits = MarioBros.DEMON_BIT;
        // El demonio no colisiona con el suelo para que flote; sólo interactúa con el personaje y ataques
        fdef.filter.maskBits = MarioBros.PERSONAJE_BIT | MarioBros.ATTACK_BIT;
        b2body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    @Override
    public void update(float dt) {
        stateTimer += dt;

        // Si está marcado para destruirse, destruye el cuerpo y marca como destruido
        if (setToDestroy && !destroyed) {
            if (b2body != null) {
                world.destroyBody(b2body);
                b2body = null;
            }
            destroyed = true;
        }

        // Si ya está destruido, oculta el sprite y sale
        if (destroyed) {
            setAlpha(0);
            return;
        }

        // Centrar el sprite respecto al cuerpo (el cuerpo está centrado, así que restamos la mitad del ancho y alto)
        if (b2body != null) {
            setPosition(b2body.getPosition().x - spriteWidth / 2,
                b2body.getPosition().y - spriteHeight / 2);
        }

        // Gestión de animaciones:
        if (isAttacking) {
            if (attackAnimation.isAnimationFinished(stateTimer)) {
                removeAttackFixture();
                isAttacking = false;
                // Reinicia el timer para que la animación idle empiece desde cero
                stateTimer = 0;
            }
            setRegion(attackAnimation.getKeyFrame(stateTimer, false));
        } else {
            setRegion(idleAnimation.getKeyFrame(stateTimer, true));
        }
    }

    // Asegúrate de declarar estas variables a nivel de clase:
    private int lives = 2;
    private boolean recentlyHit = false;

    @Override
    public void hitOnSword() {
        // Si ya se ha marcado para destruir o se recibió un golpe recientemente, no hacer nada
        if (destroyed || setToDestroy || recentlyHit) {
            return;
        }

        recentlyHit = true;
        lives--;  // Se le quita una vida
        Gdx.app.log("Demon", "Vida restante: " + lives);

        // Se pone rojo para indicar el impacto
        setColor(Color.RED);

        if (lives <= 0) {
            // Si ya no quedan vidas, se programa que, después de 0.3 segundos, se marque para destruirse
            // y se mantiene el color rojo sin resetearlo a blanco
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    setToDestroy = true;
                    recentlyHit = false;
                    b2body.applyLinearImpulse(new Vector2(.5f, 1f), b2body.getWorldCenter(), true);
                }
            }, 0.3f);
        } else {
            // Si aún le quedan vidas, se espera 0.2 segundos para volver al color normal y permitir recibir otro golpe
            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    if (!destroyed) {
                        setColor(Color.WHITE);
                    }
                    recentlyHit = false;
                }
            }, 0.2f);
        }
    }



    public void ataqueEnemigo() {
        if (destroyed || setToDestroy || b2body == null) {
            return;
        }

        // Reinicia el temporizador y activa la animación de ataque
        stateTimer = 0;
        isAttacking = true;

        // Elimina cualquier sensor de ataque previo
        removeAttackFixture();

        FixtureDef fdef = new FixtureDef();
        PolygonShape attack = new PolygonShape();
        // Definimos un sensor de ataque: aquí usamos un rectángulo que cubre un área en frente del demonio.
        // En este ejemplo, se coloca el sensor a la izquierda del cuerpo (ajusta según la dirección deseada).
        attack.set(new Vector2[]{
            new Vector2(0 / MarioBros.PPM, 10 / MarioBros.PPM),
            new Vector2(-50 / MarioBros.PPM, -20 / MarioBros.PPM),
            new Vector2(0 / MarioBros.PPM, -80 / MarioBros.PPM),
            new Vector2(-100 / MarioBros.PPM, -80 / MarioBros.PPM)
        });
        fdef.shape = attack;
        fdef.isSensor = true;
        fdef.filter.categoryBits = MarioBros.DEMONATTACK_BIT;
        attackFixture = b2body.createFixture(fdef);
        attackFixture.setUserData("ataqueDemon");

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
        // Opcionalmente, aquí podrías agregar lógica si el demonio recibe un golpe de espada
    }

    public void dispose() {
        demonAtlas.dispose();
    }
}
