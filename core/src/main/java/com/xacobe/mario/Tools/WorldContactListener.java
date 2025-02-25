package com.xacobe.mario.Tools;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Sprites.Enemy;
import com.xacobe.mario.Sprites.InteractiveTileObject;

public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixb = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixb.getFilterData().categoryBits;

        if (fixA.getUserData() == "sword" || fixb.getUserData() == "sword") {
            Fixture sword = fixA.getUserData() == "sword" ? fixA : fixb;
            Fixture object = sword == fixA ? fixb : fixA;
            if (object.getUserData() != null && object.getUserData() instanceof Enemy) {
                ((Enemy) object.getUserData()).hitOnSword();
            }
        }
        switch (cDef) {
            case MarioBros.ENEMY_BIT | MarioBros.PERSONAJE_BIT:
                if (fixA.getFilterData().categoryBits == MarioBros.ENEMY_BIT) {
                    ((Enemy) fixA.getUserData()).hitOnSword();
                }else  if (fixb.getFilterData().categoryBits == MarioBros.ENEMY_BIT) {
                    ((Enemy) fixb.getUserData()).hitOnSword();
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
