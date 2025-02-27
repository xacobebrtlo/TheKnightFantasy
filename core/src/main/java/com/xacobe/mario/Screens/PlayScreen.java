package com.xacobe.mario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Scenes.Controles;
import com.xacobe.mario.Scenes.Hud;
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
    private NoShurikenDude noShurikenDude;

    //Teclas y botones
    private Controles controles;


    public PlayScreen(MarioBros game) {
        atlas = new TextureAtlas("personaje_y_enemigos.atlas");
        this.game = game;

        gamecam = new OrthographicCamera();
        gameport = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);
        hud = new Hud(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("Magic_Cliffs_Fondo/Environment/TSX/level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);
        gamecam.position.set(gameport.getWorldWidth() / 2, gameport.getWorldHeight() / 2, 0);
        world = new World(new Vector2(0, -9.8f), true);
        b2dr = new Box2DDebugRenderer();
        new B2WorldCreator(this);
        personaje = new Personaje(this);

        //Inicializar teclas
        controles = new Controles(game.batch);

        world.setContactListener(new WorldContactListener());

        noShurikenDude = new NoShurikenDude(this, 32 / MarioBros.PPM, 170 / MarioBros.PPM);
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    @Override
    public void show() {

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
        personaje.update(dt);
        if (!noShurikenDude.destroyed) {
            noShurikenDude.update(dt);
//            noShurikenDude.ataqueEnemigo();
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
        if (!noShurikenDude.destroyed) {
            noShurikenDude.draw(game.batch);
        }
        game.batch.end();

        //Dibujar teclas
        controles.update();
        controles.render(game.batch);

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

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
