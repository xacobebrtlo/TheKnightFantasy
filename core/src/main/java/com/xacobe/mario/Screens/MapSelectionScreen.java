package com.xacobe.mario.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.xacobe.mario.MarioBros;

public class MapSelectionScreen implements Screen {
    private MarioBros game;
    private Stage stage;
    private Skin skin;

    public MapSelectionScreen(MarioBros game) {
        this.game = game;
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        // Carga un skin (puedes usar "uiskin.json" que viene con LibGDX)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Creamos una tabla para organizar los botones
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        TextButton map1Button = new TextButton("Mapa 1", skin);
        TextButton map2Button = new TextButton("Mapa 2", skin);
        TextButton map3Button = new TextButton("Mapa 3", skin);

        table.add(map1Button).pad(10);
        table.row();
        table.add(map2Button).pad(10);
        table.row();
        table.add(map3Button).pad(10);

        stage.addActor(table);

        // Listeners para cada botón
        map1Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Cambia a PlayScreen y pasa el número de mapa 1
                game.setScreen(new PlayScreen(game, 1));
                MarioBros.currentMapNumber = 1;
            }
        });

        map2Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PlayScreen(game, 2));
                MarioBros.currentMapNumber = 2;
            }
        });

        map3Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PlayScreen(game, 3));
                MarioBros.currentMapNumber = 3;
            }
        });
    }

    @Override
    public void show() {
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
