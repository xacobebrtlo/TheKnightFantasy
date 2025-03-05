package com.xacobe.mario.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.xacobe.mario.MarioBros;

public class Hud implements Disposable {
    public Stage stage;
    private Viewport viewport;

    // Agregamos una variable para la instancia del juego
    private MarioBros game;

    // Elementos del HUD (para tiempo)
    Label timeLabel;
    Label countdownLabel;

    private Integer worldTimer;
    private float timeCount;

    // Atlas y regiones para la vida (corazones)
    private TextureAtlas atlas;
    private TextureRegion heartFull, heartHalf, heartEmpty;

    // Sistema de vidas (3 vidas totales)
    private int totalLives = 3;
    private int currentLives = 3;

    // Modificamos el constructor para recibir la instancia de MarioBros
    public Hud(SpriteBatch sb, MarioBros game) {
        this.game = game;

        // Carga el atlas y extrae la región "corazonesVida"
        atlas = new TextureAtlas("Demon_and_Health.atlas");
        TextureRegion heartsRegion = atlas.findRegion("corazonesVida");
        if (heartsRegion == null) {
            Gdx.app.error("Hud", "No se encontró la región 'corazonesVida'");
        }
        // Suponemos que la región se divide en 3 partes iguales horizontalmente
        int frameWidth = heartsRegion.getRegionWidth() / 3;
        int frameHeight = heartsRegion.getRegionHeight();
        heartFull = new TextureRegion(heartsRegion, 0, 0, frameWidth, frameHeight);
        heartHalf = new TextureRegion(heartsRegion, frameWidth, 0, frameWidth, frameHeight);
        heartEmpty = new TextureRegion(heartsRegion, frameWidth * 2, 0, heartsRegion.getRegionWidth() - frameWidth * 2, frameHeight);

        // Inicializa timer (por ejemplo, 300 segundos)
        worldTimer = 0;
        timeCount = 0;

        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        // Configurar la tabla del HUD
        Table table = new Table();
        table.top();
        table.setFillParent(true);

        // Cargar la imagen de Settings desde assets
        Texture settingsTexture = new Texture(Gdx.files.internal("Settings.png"));
        ImageButton settingsButton = new ImageButton(new TextureRegionDrawable(new TextureRegion(settingsTexture)));
        // Dentro del constructor de Hud (o donde configures el settingsButton)
        // Dentro del constructor de Hud, donde configuras el settingsButton
        Skin skin = new Skin(Gdx.files.internal("uiskin.json"));
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SettingsWindow settingsWindow = new SettingsWindow(skin, game, stage);
                settingsWindow.show(getStage());
            }
        });


        // Configurar la tabla:
        // Primera celda: vacía (para los corazones a la izquierda)
        // Segunda celda: label "TIME"
        // Tercera celda: el botón de settings, alineado a la derecha
        table.add().expandX().padTop(10);
        table.add(timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE)))
            .expandX().padTop(10);
        table.add(settingsButton).expandX().padTop(10).right();

        table.row();
        // Segunda fila: se muestra el contador de tiempo en la columna central
        table.add().expandX();
        table.add(countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE)))
            .expandX();
        table.add().expandX();

        stage.addActor(table);
    }

    private Stage getStage() {
        return stage;
    }

    // Método para actualizar el timer; se llama cada frame desde PlayScreen
    public void update(float dt) {
        timeCount += dt;
        worldTimer = (int) timeCount;

        worldTimer++;  // Incrementa en lugar de decrementar
        countdownLabel.setText(String.format("%03d", worldTimer));

    }


    // Actualiza las vidas actuales (si las usas)
    public void updateLives(int lives) {
        currentLives = lives;
    }

    // Dibuja los corazones en una posición fija en la parte izquierda del HUD
    public void drawLives(SpriteBatch batch) {
        float x = 10; // margen izquierdo
        float scale = 0.1f;
        float heartWidth = heartFull.getRegionWidth() * scale;
        float heartHeight = heartFull.getRegionHeight() * scale;
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

    public int getWorldTimer() {
        return worldTimer;
    }
}
