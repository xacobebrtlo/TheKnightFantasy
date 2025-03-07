package com.xacobe.mario;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.xacobe.mario.Screens.MainMenuScreen;
import com.xacobe.mario.Screens.PlayScreen;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class MarioBros extends Game {
    public static final int V_WIDTH = 700;
    public static final int V_HEIGHT = 368;
    public static final float PPM = 100;

    //Colision bits
    public static final short GROUND_BIT = 1;
    public static final short PERSONAJE_BIT = 2;
    public static final short ENEMY_BIT = 4;
    public static final short ATTACK_BIT = 8;
    public static final short ENEMYATTACK_BIT = 16;
    public static final short COFRE_BIT = 32;
    public static final short DEMON_BIT = 64;
    public static final short DEMONATTACK_BIT = 128;
    public SpriteBatch batch;


    public static int currentMapNumber = 1;

    public static AssetManager manager;

    // Almacenamos la instancia de PlayScreen para poder reanudar el juego
    private PlayScreen playScreen;

    @Override
    public void create() {
        batch = new SpriteBatch();
        manager=new AssetManager();
        manager.load("Audio/Music/Bassoon.ogg", Music.class);
        manager.load("Audio/Sounds/select.wav", Sound.class);
        manager.load("Audio/Sounds/attack.ogg", Sound.class);
        manager.load("Audio/Sounds/enemy-death.wav", Sound.class);
        manager.load("Audio/Sounds/player-death.wav", Sound.class);
        manager.load("Audio/Sounds/jump.wav", Sound.class);
        manager.finishLoading();
        // Creamos la pantalla de juego y la asignamos
        playScreen = new PlayScreen(this);
        setScreen(new MainMenuScreen(this));
    }

    /**
     * Devuelve la instancia actual de PlayScreen para reanudar el juego.
     */
    public PlayScreen getPlayScreen() {
        return playScreen;
    }

    @Override
    public void render() {
        super.render(); // Llama al renderizado de la pantalla actual

    }

    @Override
    public void dispose() {
        batch.dispose();
        // Asegúrate de disponer también de la PlayScreen si es necesario
        playScreen.dispose();
        super.dispose();
        manager.dispose();
    }
}
