package com.xacobe.mario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
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

public class MainMenuScreen implements Screen {
    private MarioBros game;
    private Stage stage;
    private Skin skin;
    private Texture background;
    private Viewport viewport;

    public MainMenuScreen(MarioBros game) {
        this.game = game;

        // 1. Configurar el viewport y stage
        viewport = new FitViewport(800, 600);
        stage = new Stage(viewport);
        Gdx.input.setInputProcessor(stage);

        // 2. Cargar la imagen de fondo y ajustarla a la pantalla
        background = new Texture(Gdx.files.internal("ImagenFondo.PNG"));
        Image backgroundImage = new Image(background);
        backgroundImage.setFillParent(true);
        stage.addActor(backgroundImage);

        // 3. Cargar el skin por defecto (uiskin.json)
        skin = new Skin(Gdx.files.internal("uiskin.json"));

        // 4. Crear una tabla para organizar los botones
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        // 5. Crear los botones usando el estilo por defecto del skin
        TextButton playButton = new TextButton("Play", skin);
        TextButton optionsButton = new TextButton("Options", skin);
        TextButton exitButton = new TextButton("Exit", skin);

        // Añadir botones a la tabla con un tamaño y espacio adecuado
        table.add(playButton).pad(10).width(200).height(50);
        table.row();
        table.add(optionsButton).pad(10).width(200).height(50);
        table.row();
        table.add(exitButton).pad(10).width(200).height(50);

        // Agregar la tabla al stage (los actores añadidos después del fondo se dibujan encima)
        stage.addActor(table);

        // 6. Agregar listeners a los botones
        playButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                // Cambiar a la pantalla de selección de niveles
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                game.setScreen(new MapSelectionScreen(game));
            }
        });

        optionsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                game.setScreen(new OptionsScreen(game));
            }
        });


        exitButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
                Gdx.app.exit();
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
        background.dispose();
    }
}
