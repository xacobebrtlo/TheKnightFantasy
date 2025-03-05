package com.xacobe.mario.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.MapSelectionScreen;
import com.xacobe.mario.Screens.OptionsScreen;

public class SettingsWindow extends Dialog {
    private MarioBros game;
    private Skin skin;
    private Stage stageRef; // Stage donde se muestra este diálogo

    public SettingsWindow(Skin skin, final MarioBros game, Stage stage) {
        super("Settings", skin);
        this.game = game;
        this.skin = skin;
        this.stageRef = stage;
        setModal(true);
        setMovable(false);

        button("Resume", "resume");
        row();
        button("Change Level", "change");
        row();
        button("Options", "options");
        row();
        button("Exit", "exit");

        pack();
        setPosition(Gdx.graphics.getWidth()/2 - getWidth()/2,
            Gdx.graphics.getHeight()/2 - getHeight()/2);
    }

    @Override
    protected void result(Object object) {
        MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
        String res = (String) object;
        Gdx.app.log("SettingsWindow", "Opción seleccionada: " + res);

        if (res.equals("resume")) {
            hide();
            // Aquí se reanuda el juego
        } else if (res.equals("change")) {
            hide();
            // Mostrar LevelSelectionWindow (comportamiento similar a otras ventanas)
            LevelSelectionWindow levelSelectionWindow = new LevelSelectionWindow(getSkin(), game, stageRef);
            levelSelectionWindow.show(stageRef);
        } else if (res.equals("options")) {
            Gdx.app.log("SettingsWindow", "Options pressed");
            // Ocultamos esta ventana sin removerla para poder volver
            hide();
            // Abrimos OptionsWindow y le pasamos 'this' como ventana anterior
            OptionsWindow optionsWindow = new OptionsWindow(getSkin(), game, stageRef, this);
            optionsWindow.show(stageRef);
            optionsWindow.toFront();
        } else if (res.equals("exit")) {
            Gdx.app.exit();
        }
    }

    @Override
    public void hide() {
        super.hide();
        setVisible(false);
    }
}
