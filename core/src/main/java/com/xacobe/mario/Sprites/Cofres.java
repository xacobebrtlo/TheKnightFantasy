package com.xacobe.mario.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;

public class Cofres extends Sprite {
    public World world;
    public Body b2body;

    public Cofres(PlayScreen screen, float x, float y, float width, float height) {
        this.world = screen.getWorld();
        // Opcionalmente, si tienes una textura para el cofre, la asignas; si no, puedes dejarlo invisible.
        setBounds(x, y, width, height);
        defineCofre();
    }

    private void defineCofre() {
        BodyDef bdef = new BodyDef();
        // Ubicamos el cofre en el centro del rectángulo
        bdef.position.set(getX() + getWidth() / 2, getY() + getHeight() / 2);
        bdef.type = BodyDef.BodyType.StaticBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(getWidth() / 2, getHeight() / 2);
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = MarioBros.COFRE_BIT;
        fdef.filter.maskBits = MarioBros.PERSONAJE_BIT;  // Solo interactúa con el personaje
        b2body.createFixture(fdef).setUserData(this);
        //todo COREGIR COFRES

        shape.dispose();
    }
}
