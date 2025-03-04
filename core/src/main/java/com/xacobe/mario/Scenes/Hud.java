package com.xacobe.mario.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.xacobe.mario.MarioBros;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;

    // Elementos del HUD (excepto la parte de vidas)
    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label levelLabel;
    Label worldLabel;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;

    // Atlas y regiones para la vida
    private TextureAtlas atlas;
    private TextureRegion heartFull, heartHalf, heartEmpty;

    // Sistema de vidas (3 vidas totales)
    private int totalLives = 3;
    private int currentLives = 3;

    public Hud(SpriteBatch sb) {
        // Carga el atlas
        atlas = new TextureAtlas("Demon_and_Health.atlas");
        // Extrae la región "corazonesVida"
        TextureRegion heartsRegion = atlas.findRegion("corazonesVida");
        if (heartsRegion == null) {
            Gdx.app.error("Hud", "No se encontró la región 'corazonesVida'");
        }
        // Suponemos que la región se divide en 3 partes iguales horizontalmente
        int frameWidth = heartsRegion.getRegionWidth() / 3; // 901/3 ≈ 300 píxeles
        int frameHeight = heartsRegion.getRegionHeight();   // 300 píxeles
        heartFull = new TextureRegion(heartsRegion, 0, 0, frameWidth, frameHeight);
        heartHalf = new TextureRegion(heartsRegion, frameWidth, 0, frameWidth, frameHeight);
        heartEmpty = new TextureRegion(heartsRegion, frameWidth * 2, 0, heartsRegion.getRegionWidth() - frameWidth * 2, frameHeight);

        // Inicializa timer y score
        worldTimer = 300;
        timeCount = 0;
        score = 0;
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        // En lugar de mostrar "MARIO", dejamos una celda en blanco en el lado izquierdo (donde luego dibujaremos los corazones)
        table.add().expandX().padTop(10);
        table.add(worldLabel = new Label("WORLD", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).expandX().padTop(10);
        table.add(timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).expandX().padTop(10);
        table.row();
        table.add(scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE))).expandX();
        table.add(levelLabel = new Label("1-1", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).expandX();
        table.add(countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE))).expandX();

        stage.addActor(table);
    }

    // Actualiza el número de vidas actuales
    public void updateLives(int lives) {
        currentLives = lives;
    }

    // Dibuja los corazones en una posición fija en la parte izquierda del HUD
    public void drawLives(SpriteBatch batch) {
        float x = 10; // margen izquierdo
        // Escala deseada, por ejemplo, la mitad del tamaño original
        float scale = 0.2f;
        float heartWidth = heartFull.getRegionWidth() * scale;
        float heartHeight = heartFull.getRegionHeight() * scale;
        // Coloca los corazones en la parte superior con un margen
        float y = MarioBros.V_HEIGHT - heartHeight - 10;
        for (int i = 0; i < totalLives; i++) {
            if (i < currentLives) {
                batch.draw(heartFull, x + i * (heartWidth + 5), y, heartWidth, heartHeight);
            } else {
                batch.draw(heartEmpty, x + i * (heartWidth + 5), y, heartWidth, heartHeight);
            }
        }
    }


    @Override
    public void dispose() {
        stage.dispose();
        atlas.dispose();
    }
}
