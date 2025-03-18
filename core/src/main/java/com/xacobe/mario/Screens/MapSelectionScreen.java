package com.xacobe.mario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.xacobe.mario.MarioBros;

public class MapSelectionScreen implements Screen {
    private MarioBros game;
    private Stage stage;
    private Skin skin;
    private Texture background;

    public MapSelectionScreen(MarioBros game) {
        this.game = game;
        // Configurar el viewport y stage
        Viewport viewport = new FitViewport(800, 600);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        // Cargar la imagen de fondo
        background = new Texture(Gdx.files.internal("ImagenFondo.PNG"));
        Image backgroundImage = new Image(background);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // Cargar el skin por defecto (asegúrate de que uiskin.json existe en assets)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear una tabla para organizar los botones
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // Crear botones usando el estilo por defecto del skin con mayor tamaño
        TextButton level1Button = new TextButton("Level 1", skin);
        TextButton level2Button = new TextButton("Level 2", skin);
//        TextButton level3Button = new TextButton("Level 3", skin);
        TextButton backButton   = new TextButton("Back", skin);

        // Agregar botones a la tabla con mayor ancho y alto
        table.add(level1Button).width(200).height(50).pad(10);
        table.row();
        table.add(level2Button).width(200).height(50).pad(10);
        table.row();
//        table.add(level3Button).width(200).height(50).pad(10);
//        table.row();
        table.add(backButton).width(200).height(50).pad(10);

        stage.addActor(table);

        // Agregar listeners a cada botón
        level1Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PlayScreen(game, 1));
                MarioBros.currentMapNumber = 1;
            }
        });
        level2Button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                game.setScreen(new PlayScreen(game, 2));
                MarioBros.currentMapNumber = 2;
            }
        });
//
        backButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Vuelve a la pantalla principal
                game.setScreen(new MainMenuScreen(game));
            }
        });
    }

    @Override
    public void show() { }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0,0,0,1);
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
