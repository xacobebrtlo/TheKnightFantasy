package com.xacobe.mario.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.MapSelectionScreen;

public class SettingsWindow extends Dialog {
    private MarioBros game;
    private Skin skin;

    public SettingsWindow(Skin skin, final MarioBros game) {
        super("Settings", skin);
        this.game = game;
        this.skin = skin;
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
        setPosition(Gdx.graphics.getWidth() / 2 - getWidth() / 2,
            Gdx.graphics.getHeight() / 2 - getHeight() / 2);
    }

    @Override
    protected void result(Object object) {
        String res = (String) object;
        Gdx.app.log("SettingsWindow", "Opción seleccionada: " + res);

        if (res.equals("resume")) {
            // Oculta y remueve el diálogo para reanudar el juego
            hide();
            remove();
            // Dentro de SettingsWindow, en result(), opción "change":
        } else if (res.equals("change")) {
            hide();
            // Supongamos que el diálogo SettingsWindow ya está en un stage válido, obtenemos ese stage
            Stage currentStage = getStage();
            // Si getStage() es null (porque se removió), podrías haberlo pasado desde el HUD.
            // Aquí asumiremos que lo tienes disponible, o bien lo obtienes de otra manera.
            LevelSelectionWindow levelSelectionWindow = new LevelSelectionWindow(getSkin(), game, currentStage);
            levelSelectionWindow.show(currentStage);
        } else if (res.equals("options")) {
            Gdx.app.log("SettingsWindow", "Options pressed");
            hide();
            remove();
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
