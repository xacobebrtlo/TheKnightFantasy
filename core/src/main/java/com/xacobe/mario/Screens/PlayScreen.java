package com.xacobe.mario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Scenes.Controles;
import com.xacobe.mario.Scenes.Hud;
import com.xacobe.mario.Sprites.Demon;
import com.xacobe.mario.Sprites.NoShurikenDude;
import com.xacobe.mario.Sprites.Personaje;
import com.xacobe.mario.Tools.B2WorldCreator;
import com.xacobe.mario.Tools.WorldContactListener;

public class PlayScreen implements Screen {
    private MarioBros game;
    private TextureAtlas atlas;
    private OrthographicCamera gamecam;
    private Viewport gameport;
    private Hud hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //box2D variables
    private World world;
    private Box2DDebugRenderer b2dr;

    private Personaje personaje;

    private B2WorldCreator creator;


    //Teclas y botones
    private Controles controles;

    public static Music music;
    private int mapNumber = 1;

    public PlayScreen(MarioBros game, int mapnumber) {
        atlas = new TextureAtlas("personaje_y_enemigos.atlas");
        this.game = game;

        this.mapNumber = mapnumber;
        MarioBros.currentMapNumber = this.mapNumber;

        gamecam = new OrthographicCamera();
        gameport = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);
        hud = new Hud(game.batch, game);

        this.mapNumber = mapnumber;
        String mapFile = "Magic_Cliffs_Fondo/Environment/TSX/level1.tmx";
        if (mapNumber == 2) {
            mapFile = "Old-dark-Castle-tileset-Files/TSX/level2.tmx";
        } else if (mapNumber == 3) {
            mapFile = "Magic_Cliffs_Fondo/Environment/TSX/level3.tmx";
        }
        mapLoader = new TmxMapLoader();
        map = mapLoader.load(mapFile);
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gamecam.position.set(gameport.getWorldWidth() / 2, gameport.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, -9.8f), true);
        b2dr = new Box2DDebugRenderer();
        creator = new B2WorldCreator(this);
        personaje = new Personaje(this);

        //Inicializar teclas
        controles = new Controles(game.batch);

        world.setContactListener(new WorldContactListener());
        music = MarioBros.manager.get("Audio/Music/Bassoon.ogg", Music.class);
        music.setLooping(true);
        music.play();


    }

    public PlayScreen(MarioBros game) {
        this(game, MarioBros.currentMapNumber);
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    private Timer.Task enemyAttackTask;
    private Timer.Task demonAttackTask;


    @Override
    public void show() {
        InputMultiplexer multiplexer = new InputMultiplexer();
        // Agrega el stage del juego (si lo tienes) y el del HUD
        multiplexer.addProcessor(hud.stage);
        multiplexer.addProcessor(controles.stage); // O el InputProcessor que maneje la lógica del juego
        MarioBros.manager.get("Audio/Sounds/select.wav", Sound.class).play();
        Gdx.input.setInputProcessor(multiplexer);
        // Programamos una tarea que se ejecute 4 segundos después y luego cada 4 segundos
        enemyAttackTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {
                for (NoShurikenDude noShurikenDude : creator.getNoshurikenDUdes()) {
                    if (!noShurikenDude.destroyed) {
                        noShurikenDude.isAttacking = true;
                        noShurikenDude.ataqueEnemigo();
                    }
                }
            }
        }, 1, 4);
        demonAttackTask = Timer.schedule(new Timer.Task() {
            @Override
            public void run() {

                for (Demon demon : creator.getDemons()) {
                    if (!demon.destroyed) {
                        demon.isAttacking = true;
                        demon.ataqueEnemigo();
                    }
                }
            }

        }, 1, 4);
    }


    public void handleInput(float dt) {

        //Atacar saltando
        if (controles.isAttackPressed() && personaje.b2body.getLinearVelocity().y > 0 && personaje.runningRight) {
            Personaje.isJumpAttack = true;
            Personaje.stateTimer = 0;
            personaje.hitBoxAtaque();

        } else if (controles.isAttackPressed() && personaje.b2body.getLinearVelocity().y > 0 && !personaje.runningRight) {
            Personaje.isJumpAttack = true;
            Personaje.stateTimer = 0;
            personaje.hitBoxAtaque();

            //Atacar agachado
        } else if (controles.isAttackPressed() && controles.isCrouching()) {
            Personaje.isAttacking = true;
            Personaje.iscrouching = true;
            Personaje.stateTimer = 0;
            personaje.hitBoxAtaque();

            //Atacar
        } else if (controles.isAttackPressed()) {
            personaje.b2body.applyLinearImpulse(new Vector2(0, 0), personaje.b2body.getWorldCenter(), true);
            Personaje.stateTimer = 0;
            Personaje.isAttacking = true;
            personaje.hitBoxAtaque();

            //Movimiento normal
        } else if (controles.isJumpPressed() && personaje.b2body.getLinearVelocity().y == 0) {
            personaje.b2body.applyLinearImpulse(new Vector2(0, 4f), personaje.b2body.getWorldCenter(), true);
        } else if (controles.isMoveRight()) {
            personaje.b2body.setLinearVelocity(1.8f, personaje.b2body.getLinearVelocity().y);
        } else if (controles.isMoveLeft()) {
            personaje.b2body.setLinearVelocity(-1.8f, personaje.b2body.getLinearVelocity().y);
        } else {
            personaje.b2body.setLinearVelocity(0, personaje.b2body.getLinearVelocity().y);
        }
        Personaje.iscrouching = controles.isCrouching();

    }


    public void update(float dt) {
        handleInput(dt);
        world.step(1 / 60f, 6, 2);
        hud.update(dt);
        personaje.update(dt);
        for (NoShurikenDude noShurikenDude : creator.getNoshurikenDUdes()) {
            if (!noShurikenDude.destroyed) {
                noShurikenDude.update(dt);
            }
        }
        // Actualiza los demon
        for (Demon demon : creator.getDemons()) {
            demon.update(dt);
        }
        // Cuando el personaje gana (toca el cofre)
        if (Personaje.playerWin) {
            // Supongamos que el timer de HUD indica el tiempo restante o el tiempo transcurrido.
            // Por ejemplo, si "worldTimer" es el tiempo que tardaste:
            int levelTime = hud.getWorldTimer(); // Asegúrate de que este método exista y devuelva el tiempo (en segundos)
            int levelLives = personaje.lives;    // Vidas restantes del personaje

            Records records = new Records();
            records.updateRecord(MarioBros.currentMapNumber, levelTime, levelLives);

            // Luego, cambiar a la pantalla de victoria o al menú principal
            game.setScreen(new WinScreen(game));
        }

        gamecam.position.x = personaje.b2body.getPosition().x;
        gamecam.update();
        renderer.setView(gamecam);
    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //mapa
        renderer.render();

        //box2D
        b2dr.render(world, gamecam.combined);
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        personaje.draw(game.batch);


        for (NoShurikenDude noShurikenDude : creator.getNoshurikenDUdes()) {
            if (!noShurikenDude.destroyed) {
                noShurikenDude.draw(game.batch);
            }
        }

        game.batch.end();


        // Dibuja los demon
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        for (Demon demon : creator.getDemons()) {
            demon.draw(game.batch);
        }
        game.batch.end();

        //Dibujar teclas
        controles.update();
        controles.render(game.batch);

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        // Actualizamos y dibujamos el HUD
        hud.updateLives(personaje.lives); // Asumiendo que lives es una variable en Personaje
        hud.stage.draw();
        game.batch.begin();
        hud.drawLives(game.batch);
        game.batch.end();

        if (gameOver()) {
            game.setScreen(new GameOverScreen(game));
//            dispose();

        }


    }

    public Boolean gameOver() {
        return ((personaje.currentState == Personaje.State.DEAD || personaje.lives <= 0) && personaje.getStateTimer() > 0.5f);

    }

    @Override
    public void resize(int width, int height) {
        gameport.update(width, height);
        controles.resize(width, height);
    }


    public TiledMap getMap() {
        return map;
    }

    public World getWorld() {
        return world;
    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {
        if (enemyAttackTask != null) {
            enemyAttackTask.cancel();
        }
        if (demonAttackTask != null) {
            demonAttackTask.cancel();
        }
    }

    @Override
    public void dispose() {

        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}
