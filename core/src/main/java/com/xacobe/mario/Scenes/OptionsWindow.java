package com.xacobe.mario.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.PlayScreen;
import com.xacobe.mario.Screens.Records;

public class OptionsWindow extends Dialog {
    private MarioBros game;
    private Stage stageRef; // Stage en el que se mostrará este diálogo
    private Dialog previousDialog; // Ventana anterior (SettingsWindow)

    public OptionsWindow(Skin skin, final MarioBros game, Stage stage, Dialog previousDialog) {
        super("Options", skin);
        this.game = game;
        this.stageRef = stage;
        this.previousDialog = previousDialog;
        setModal(true);
        setMovable(false);

        // Crear botones para cada opción
        final TextButton muteButton = new TextButton("Mute", skin);
        TextButton recordsButton = new TextButton("Records", skin);
        TextButton helpButton = new TextButton("Help", skin);
        TextButton languagesButton = new TextButton("Languages", skin);
        TextButton backButton = new TextButton("Back", skin);

        // Distribuir los botones horizontalmente en la contentTable
        getContentTable().pad(20);
        getContentTable().add(muteButton).pad(10).width(100).height(40).align(Align.center);
        getContentTable().add(recordsButton).pad(10).width(100).height(40).align(Align.center);
        getContentTable().add(helpButton).pad(10).width(100).height(40).align(Align.center);
        getContentTable().add(languagesButton).pad(10).width(100).height(40).align(Align.center);
        getContentTable().row();

        // En la buttonTable se coloca el botón Back centrado
        getButtonTable().pad(10);
        getButtonTable().add(backButton).width(200).height(50).pad(10);

        // Listener para Mute: alterna entre pausar y reproducir la música
        muteButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                if (PlayScreen.music != null && PlayScreen.music.isPlaying()) {
                    PlayScreen.music.pause();
                    muteButton.setText("Unmute");
                } else if (PlayScreen.music != null) {
                    PlayScreen.music.play();
                    muteButton.setText("Mute");
                }
            }
        });

        // Listener para Records: muestra el contenido de la clase Records
        recordsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                Records records = new Records();
                String recordsContent = records.getAllRecords(3); // Ajusta según niveles
                Dialog recDialog = new Dialog("Records", skin) {
                    @Override
                    protected void result(Object object) {
                        // Cierra el diálogo al pulsar Close
                        hide();
                        remove();
                    }
                };
                recDialog.text(new Label(recordsContent, skin));
                recDialog.button("Close", true);
                recDialog.show(stageRef);
            }
        });

        // Listener para Help: muestra información de ayuda
        helpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                Dialog helpDialog = new Dialog("Help", skin) {
                    @Override
                    protected void result(Object object) {
                        hide();
                        remove();
                    }
                };
                helpDialog.text("Use arrow keys to move, jump button to jump and the attack button to attack.\nCollect coins and defeat enemies to progress through the levels.");
                helpDialog.button("Close", true);
                helpDialog.show(stageRef);
            }
        });

        // Listener para Languages: muestra opciones de idiomas
        languagesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                Dialog langDialog = new Dialog("Languages", skin) {
                    @Override
                    protected void result(Object object) {
                        hide();
                        remove();
                    }
                };
                langDialog.text("Select your language:\n - English\n - Spanish\n - French");
                langDialog.button("Close", true);
                langDialog.show(stageRef);
            }
        });

        // Listener para Back: cierra OptionsWindow y vuelve a la ventana anterior
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                hide(); // Oculta OptionsWindow
                remove();
                // En lugar de re-mostrar la ventana anterior oculta, creamos una nueva instancia de SettingsWindow.
                SettingsWindow settingsWindow = new SettingsWindow(getSkin(), game, stageRef);
                settingsWindow.show(stageRef);
            }
        });


        pack();
        setSize(400, 300); // Tamaño más pequeño
        setPosition(Gdx.graphics.getWidth()/2 - getWidth()/2,
            Gdx.graphics.getHeight()/2 - getHeight()/2);
    }
}
