package com.xacobe.mario.Tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Sprites.Demon;
import com.xacobe.mario.Sprites.Enemy;
import com.xacobe.mario.Sprites.InteractiveTileObject;
import com.xacobe.mario.Sprites.NoShurikenDude;
import com.xacobe.mario.Sprites.Personaje;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef) {
            // Caso 1: Sensor de ataque del personaje + enemigo
            case MarioBros.ATTACK_BIT | MarioBros.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ATTACK_BIT
                    && fixB.getUserData() != null
                    && fixB.getUserData() instanceof Enemy) {

                    Enemy enemy = (Enemy) fixB.getUserData(); // Generaliza primero

                    if (enemy instanceof Demon) { // ⚠️ Ahora verificamos si es Demon
                        Demon demon = (Demon) enemy;
                        if (!demon.destroyed && !demon.setToDestroy) {
                            demon.hitOnSword();
                        }
                    } else if (enemy instanceof NoShurikenDude) { // ⚠️ También verificamos si es NoShurikenDude
                        NoShurikenDude noShurikenDude = (NoShurikenDude) enemy;
                        if (!noShurikenDude.destroyed && !noShurikenDude.setToDestroy) {
                            noShurikenDude.hitOnSword();
                        }
                    }
                }
                break;

            // Caso 2: Sensor de ataque del enemigo + personaje
            case MarioBros.ENEMYATTACK_BIT | MarioBros.PERSONAJE_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMYATTACK_BIT
                    && fixB.getUserData() != null
                    && fixB.getUserData() instanceof Personaje) {
                    Personaje p = (Personaje) fixB.getUserData();
                    p.onEnemyHit();
                } else if (fixB.getFilterData().categoryBits == MarioBros.ENEMYATTACK_BIT
                    && fixA.getUserData() != null
                    && fixA.getUserData() instanceof Personaje) {
                    Personaje p = (Personaje) fixA.getUserData();
                    p.onEnemyHit();
                }
                break;
            //caso 3 personaje choca contra enemigo
            case MarioBros.PERSONAJE_BIT | MarioBros.ENEMY_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.PERSONAJE_BIT &&
                    fixA.getUserData() != null && fixA.getUserData() instanceof Personaje) {
                    ((Personaje) fixA.getUserData()).collidingWithEnemy = true;
                }
                if (fixB.getFilterData().categoryBits == MarioBros.PERSONAJE_BIT &&
                    fixB.getUserData() != null && fixB.getUserData() instanceof Personaje) {
                    ((Personaje) fixB.getUserData()).collidingWithEnemy = true;
                }
                break;

            //TODO cuando toque el cofre cambiar estado FINPARTIDA y Mostrar menu principal
            case MarioBros.PERSONAJE_BIT | MarioBros.COFRE_BIT:
                if (fixA.getUserData() != null && fixA.getUserData() instanceof Personaje) {
                    ((Personaje) fixA.getUserData()).currentState = Personaje.State.DEAD;
                    Personaje p = (Personaje) fixA.getUserData();
                    p.lives = 0;
                } else if (fixB.getUserData() != null && fixB.getUserData() instanceof Personaje) {
                    ((Personaje) fixB.getUserData()).currentState = Personaje.State.DEAD;
                    Personaje p = (Personaje) fixB.getUserData();
                    p.lives = 0;
                }
                break;
            case MarioBros.ATTACK_BIT | MarioBros.DEMON_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ATTACK_BIT
                    && fixB.getUserData() != null
                    && fixB.getUserData() instanceof Demon) {

                    Demon enemy = (Demon) fixB.getUserData();
                    if (!enemy.destroyed && !enemy.setToDestroy) {
                        enemy.hitOnSword();
                    }

                } else if (fixB.getFilterData().categoryBits == MarioBros.ATTACK_BIT
                    && fixA.getUserData() != null
                    && fixA.getUserData() instanceof Demon) {

                    Demon enemy = (Demon) fixA.getUserData();
                    if (!enemy.destroyed && !enemy.setToDestroy) {
                        enemy.hitOnSword();
                    }
                }
                break;

            // Otros casos...
        }
    }


    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;
        // Cuando se termina el contacto entre el personaje y el enemigo, resetea la bandera.
        if (cDef == (MarioBros.PERSONAJE_BIT | MarioBros.ENEMY_BIT)) {
            if (fixA.getUserData() != null && fixA.getUserData() instanceof Personaje) {
                ((Personaje) fixA.getUserData()).collidingWithEnemy = false;
            }
            if (fixB.getUserData() != null && fixB.getUserData() instanceof Personaje) {
                ((Personaje) fixB.getUserData()).collidingWithEnemy = false;
            }

        }
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
