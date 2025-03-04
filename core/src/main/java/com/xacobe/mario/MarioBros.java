package com.xacobe.mario;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.xacobe.mario.Screens.MainMenuScreen;
import com.xacobe.mario.Screens.PlayScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class MarioBros extends Game {
    public static final int V_WIDTH =700;
    public static final int V_HEIGHT = 368;
    public static  final float PPM=100;

    //Colision bits
    public static final short GROUND_BIT =1;
    public static final short PERSONAJE_BIT=2;
    public static final short ENEMY_BIT=4;
    public static final short ATTACK_BIT=8;
    public static final short ENEMYATTACK_BIT=16;
    public static final short COFRE_BIT=32;
    public static final short DEMON_BIT=64;
    public static final short DEMONATTACK_BIT=128;
    public SpriteBatch batch;
    public static int currentMapNumber = 1;
    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new MainMenuScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

}
