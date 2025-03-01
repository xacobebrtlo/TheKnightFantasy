package com.xacobe.mario.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Sprites.Enemy;
import com.xacobe.mario.Sprites.InteractiveTileObject;
import com.xacobe.mario.Sprites.Personaje;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // Combina las categorías de ambos fixtures:
        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch(cDef) {
            // Caso 1: Sensor de ataque del personaje + enemigo
            case MarioBros.ATTACK_BIT | MarioBros.ENEMY_BIT:
                // Si fixA es el sensor de ataque del personaje...
                if(fixA.getFilterData().categoryBits == MarioBros.ATTACK_BIT &&
                    fixB.getUserData() != null && fixB.getUserData() instanceof Enemy) {
                    ((Enemy) fixB.getUserData()).hitOnSword();
                }
                // Si fixB es el sensor de ataque del personaje...
                else if(fixB.getFilterData().categoryBits == MarioBros.ATTACK_BIT &&
                    fixA.getUserData() != null && fixA.getUserData() instanceof Enemy) {
                    ((Enemy) fixA.getUserData()).hitOnSword();
                }
                break;

            // Caso 2: Sensor de ataque del enemigo + personaje
            case MarioBros.ENEMYATTACK_BIT | MarioBros.PERSONAJE_BIT:
                // Si fixA es el sensor de ataque del enemigo...
                if(fixA.getFilterData().categoryBits == MarioBros.ENEMYATTACK_BIT &&
                    fixB.getUserData() != null && fixB.getUserData() instanceof Personaje) {
                    ((Personaje) fixB.getUserData()).onEnemyHit();
                }
                // Si fixB es el sensor de ataque del enemigo...
                else if(fixB.getFilterData().categoryBits == MarioBros.ENEMYATTACK_BIT &&
                    fixA.getUserData() != null && fixA.getUserData() instanceof Personaje) {
                    ((Personaje) fixA.getUserData()).onEnemyHit();
                }
                break;
        }
    }


    @Override
    public void endContact(Contact contact) {

    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
