package com.xacobe.mario.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.xacobe.mario.MarioBros;

public class Personaje extends Sprite {
    public World world;
    public Body b2body;

    public Personaje(World world){
        this.world=world;
        definePersonaje();
    }

    public  void definePersonaje(){
        BodyDef bdef=new BodyDef();
        bdef.position.set(32/ MarioBros.PPM,170/MarioBros.PPM);
        bdef.type=BodyDef.BodyType.DynamicBody;
        b2body=world.createBody(bdef);

        FixtureDef fdef=new FixtureDef();
        CircleShape shape= new CircleShape();
        shape.setRadius(5/MarioBros.PPM);

        fdef.shape=shape;
        b2body.createFixture(fdef);
    }
}
