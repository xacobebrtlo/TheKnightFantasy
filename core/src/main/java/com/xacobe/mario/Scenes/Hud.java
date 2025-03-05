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
    private MarioBros game;

    // Elementos del HUD para el tiempo
    Label timeLabel;
    Label countdownLabel;

    private int worldTimer;
    private float timeCount;

    // Atlas y regiones para la vida (corazones)
    private TextureAtlas atlas;
    private TextureRegion heartFull, heartHalf, heartEmpty;

    // Sistema de vidas (3 vidas totales)
    private int totalLives = 3;
    private int currentLives = 3;

    public Hud(SpriteBatch sb, MarioBros game) {
        this.game = game;

        // Cargar el atlas y extraer la región "corazonesVida"
        atlas = new TextureAtlas("Demon_and_Health.atlas");
        TextureRegion heartsRegion = atlas.findRegion("corazonesVida");
        if (heartsRegion == null) {
            Gdx.app.error("Hud", "No se encontró la región 'corazonesVida'");
        }
        int frameWidth = heartsRegion.getRegionWidth() / 3;
        int frameHeight = heartsRegion.getRegionHeight();
        heartFull = new TextureRegion(heartsRegion, 0, 0, frameWidth, frameHeight);
        heartHalf = new TextureRegion(heartsRegion, frameWidth, 0, frameWidth, frameHeight);
        heartEmpty = new TextureRegion(heartsRegion, frameWidth * 2, 0, heartsRegion.getRegionWidth() - frameWidth * 2, frameHeight);

        // Inicializar timer
        worldTimer = 0;
        timeCount = 0;

        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, sb);

        // Crear la tabla principal para el HUD
        Table table = new Table();
        table.top();
        table.setFillParent(true);
        // No se agrega padding superior para que la fila quede lo más arriba posible
        table.padTop(-20);

        // Cargar el skin para los elementos UI
        final Skin skin = new Skin(Gdx.files.internal("uiskin.json"));

        // Crear el botón de Settings con zona táctil ampliada usando createButton
        ImageButton settingsButton = createButton("Settings.png", 100, 100, 35);
        settingsButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                SettingsWindow settingsWindow = new SettingsWindow(skin, game, stage);
                settingsWindow.show(getStage());
            }
        });

        // Crear una tabla interna para centrar verticalmente "TIME" y el contador
        Table timerTable = new Table();
        timerTable.center();
        timerTable.add(timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE))).padBottom(5).padLeft(80);
        timerTable.row();
        timerTable.add(countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE))).padLeft(80);

        // La fila principal del HUD:
        // - Primera celda: vacía (los corazones se dibujan por fuera)
        // - Segunda celda: la tabla interna con el timer
        // - Tercera celda: el botón de Settings
        table.add().expandX();
        table.add(timerTable).expandX().center();
        table.add(settingsButton).expandX().right().size(100, 100);

        stage.addActor(table);
    }

    /**
     * Crea un ImageButton a partir de una textura, definiendo su tamaño visual y ampliando su zona táctil.
     * @param texturePath Ruta de la imagen.
     * @param width Tamaño visual (ancho).
     * @param height Tamaño visual (alto).
     * @param extra Píxeles extra para ampliar la zona táctil.
     * @return ImageButton configurado.
     */
    private ImageButton createButton(String texturePath, float width, float height, final float extra) {
        Texture texture = new Texture(Gdx.files.internal(texturePath));
        TextureRegion region = new TextureRegion(texture);
        TextureRegionDrawable drawable = new TextureRegionDrawable(region);
        ImageButton button = new ImageButton(drawable) {
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                if (x < -extra || x > getWidth() + extra || y < -extra || y > getHeight() + extra)
                    return null;
                return this;
            }
        };
        button.setSize(width, height);
        return button;
    }

    private Stage getStage() {
        return stage;
    }

    // Actualiza el timer; se llama cada frame desde PlayScreen
    public void update(float dt) {
        timeCount += dt;
        worldTimer = (int) timeCount;
        countdownLabel.setText(String.format("%03d", worldTimer));
    }

    // Actualiza las vidas actuales (si se usan)
    public void updateLives(int lives) {
        currentLives = lives;
    }

    // Dibuja los corazones en la parte izquierda del HUD
    public void drawLives(SpriteBatch batch) {
        float x = 10;
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
