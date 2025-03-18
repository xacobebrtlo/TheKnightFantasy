package com.xacobe.mario.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;
import com.badlogic.gdx.audio.Sound;

public class LevelSelectionWindow extends Dialog {
    private MarioBros game;
    private Skin skin;
    private Stage stageRef; // Referencia al stage que usaremos

    public LevelSelectionWindow(Skin skin, final MarioBros game, Stage stageRef) {
        super("Select Level", skin);
        this.game = game;
        this.skin = skin;
        this.stageRef = stageRef;
        setModal(true);
        setMovable(false);

        button("Level 1", "level1");
        row();
        button("Level 2", "level2");
        row();
//        button("Level 3", "level3");
//        row();
        button("Back", "back");

        pack();
        setPosition(Gdx.graphics.getWidth()/2 - getWidth()/2,
            Gdx.graphics.getHeight()/2 - getHeight()/2);
    }

    @Override
    protected void result(Object object) {
        // Reproduce el sonido de selección al pulsar cualquier botón
        MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();

        String res = (String) object;
        Gdx.app.log("LevelSelectionWindow", "Opción seleccionada: " + res);

        if(res.equals("back")){
            hide();
            remove();
            // Crea una nueva instancia de SettingsWindow y la muestra usando el stage recibido
            SettingsWindow settingsWindow = new SettingsWindow(skin, game,stageRef);
            settingsWindow.show(stageRef);
        } else if (res.equals("level1")){
            game.setScreen(new PlayScreen(game, 1));
        } else if (res.equals("level2")){
            game.setScreen(new PlayScreen(game, 2));
//        } else if (res.equals("level3")){
//            game.setScreen(new PlayScreen(game, 3));
//        }
    }}
}
