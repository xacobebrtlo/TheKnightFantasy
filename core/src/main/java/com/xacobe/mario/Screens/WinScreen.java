package com.xacobe.mario.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.xacobe.mario.MarioBros;

public class WinScreen implements Screen {
    private MarioBros game;
    private Stage stage;

    public WinScreen(MarioBros game) {
        this.game = game;
        // Usamos ScreenViewport para que se ajuste al tamaño de la pantalla
        stage = new Stage(new ScreenViewport());

        // Creamos un estilo para la etiqueta usando una fuente por defecto
        BitmapFont font = new BitmapFont();
        Label.LabelStyle style = new Label.LabelStyle(font, com.badlogic.gdx.graphics.Color.WHITE);

        // Creamos la etiqueta con el mensaje de victoria
        Label winLabel = new Label("YOU WON THE LEVEL", style);

        // Usamos una Table para centrar la etiqueta
        Table table = new Table();
        table.setFillParent(true);
        table.center();
        table.add(winLabel);

        stage.addActor(table);

        // Configuramos un InputListener para detectar el clic y pasar al menú principal
        stage.addListener(new ClickListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                game.setScreen(new MainMenuScreen(game));
                dispose();
                return true;
            }
        });

        // También podemos asignar el Stage como InputProcessor
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void show() {
    }

    @Override
    public void render(float delta) {
        // Limpiamos la pantalla
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Actualizamos y dibujamos el Stage
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        // Actualiza el viewport para que la Table se mantenga centrada
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
    }
}
