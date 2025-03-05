package com.xacobe.mario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Screens.Records;

public class OptionsScreen implements Screen {
    private MarioBros game;
    private Stage stage;
    private Skin skin;
    private Texture background; // Para el fondo

    public OptionsScreen(MarioBros game) {
        this.game = game;
        stage = new Stage(new FitViewport(800, 600));
        Gdx.input.setInputProcessor(stage);
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Cargar la imagen de fondo y ajustarla a la pantalla
        background = new Texture(Gdx.files.internal("ImagenFondo.PNG"));
        Image backgroundImage = new Image(background);
        backgroundImage.setFillParent(true);
        // Se añade primero para que quede en el fondo
        stage.addActor(backgroundImage);

        // Crear tabla para organizar botones
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Crear botones para cada opción
        final TextButton muteButton = new TextButton("Mute", skin);
        TextButton recordsButton = new TextButton("Records", skin);
        TextButton helpButton = new TextButton("Help", skin);
        TextButton languagesButton = new TextButton("Languages", skin);
        TextButton backButton = new TextButton("Back", skin);

        table.add(muteButton).pad(10).width(200).height(50);
        table.row();
        table.add(recordsButton).pad(10).width(200).height(50);
        table.row();
        table.add(helpButton).pad(10).width(200).height(50);
        table.row();
        table.add(languagesButton).pad(10).width(200).height(50);
        table.row();
        table.add(backButton).pad(10).width(200).height(50);

        stage.addActor(table);

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
                // Se asume que tienes 3 niveles; ajusta este valor según tus necesidades.
                String recordsContent = records.getAllRecords(3);
                Dialog dialog = new Dialog("Records", skin);
                dialog.text(new Label(recordsContent, skin));
                dialog.button("Close", true);
                dialog.show(stage);
            }
        });

        // Listener para Help: muestra información de ayuda
        helpButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                Dialog dialog = new Dialog("Help", skin);
                dialog.text("Use arrow keys to move, jump button to jump and the attack button to attack.\nDefeat enemies to progress through the levels.");
                dialog.button("Close", true);
                dialog.show(stage);
            }
        });

        // Listener para Languages: muestra opciones de idiomas
        languagesButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                Dialog dialog = new Dialog("Languages", skin);
                dialog.text("Select your language:\n - English\n - Spanish\n - French");
                dialog.button("Close", true);
                dialog.show(stage);
            }
        });

        // Listener para Back: regresa al menú principal
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                game.setScreen(new MainMenuScreen(game));
                dispose();
            }
        });
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() { }

    @Override
    public void resume() { }

    @Override
    public void hide() { }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
        background.dispose();
    }
}
