package com.xacobe.mario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.xacobe.mario.MarioBros;

public class MainMenuScreen implements Screen {
    private MarioBros game;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(MarioBros game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        // Carga un skin (puedes usar uiskin.json incluido en LibGDX o tu propio skin)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crea una tabla para organizar los botones
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Crea los botones del menú
        TextButton playButton = new TextButton("Play", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Agrega los botones a la tabla con algo de padding
        table.add(playButton).pad(10);
        table.row();
        table.add(optionsButton).pad(10);
        table.row();
        table.add(exitButton).pad(10);

        // Agrega la tabla al stage
        stage.addActor(table);

        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new MapSelectionScreen(game));
            }
        });

        // Listener para Options: aquí puedes implementar la pantalla de opciones
        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Por ejemplo: game.setScreen(new OptionsScreen(game));
            }
        });

        // Listener para Exit: sale de la aplicación
        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                Gdx.app.exit();
            }
        });
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Limpia la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualiza y dibuja el stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
