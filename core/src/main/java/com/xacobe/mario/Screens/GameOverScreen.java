package com.xacobe.mario.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.xacobe.mario.MarioBros;

public class GameOverScreen implements Screen {
    private Stage stage;
    private Game game;

    public GameOverScreen(Game game) {
        this.game = game;
        stage = new Stage(new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera()), ((MarioBros) game).batch);
        Label.LabelStyle style = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Table table = new Table();
        table.setFillParent(true);
        table.center();

        Label gameOverLabel = new Label("GAME OVER", style);
        Label playAgain = new Label("Click to Play Again", style);
        table.add(gameOverLabel).expandX().padTop(10);
        table.row();
        table.add(playAgain).expandX().padTop(10);
        stage.addActor(table);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if (Gdx.input.justTouched()) {
            // Cambia a la pantalla de selección de mapas o PlayScreen sin llamar a dispose() manualmente
            game.setScreen(new PlayScreen((MarioBros) game));
            return;
        }
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
        // No disposes del batch aquí
    }
}
