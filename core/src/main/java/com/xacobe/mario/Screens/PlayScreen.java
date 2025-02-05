package com.xacobe.mario.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.xacobe.mario.MarioBros;
import com.xacobe.mario.Scenes.Hud;
import com.xacobe.mario.Sprites.Personaje;

public class PlayScreen implements Screen {
    private MarioBros game;

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

    public PlayScreen(MarioBros game) {
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

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fded = new FixtureDef();
        Body body;

        personaje = new Personaje(world);
        //Suelo Rectangulos
        for (MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle rec = ((RectangleMapObject) object).getRectangle();
            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rec.getX() + rec.getWidth() / 2) / MarioBros.PPM, (rec.getY() + rec.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rec.getWidth() / 2 / MarioBros.PPM, rec.getHeight() / 2 / MarioBros.PPM);
            fded.shape = shape;
            body.createFixture(fded);
        }

        //Suelo Rampa Poligono
        //polygon
        for (MapObject object : map.getLayers().get(6).getObjects().getByType(PolygonMapObject.class)) {
            Polygon polygon = ((PolygonMapObject) object).getPolygon();
            BodyDef bodyDef = new BodyDef();
            FixtureDef fixtureDef = new FixtureDef();
            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set(polygon.getOriginX(), polygon.getOriginY());
            Body bodyPol = world.createBody(bodyDef);
            PolygonShape polygonShape = new PolygonShape();
            fixtureDef.shape = convertPolygonToPolygonShape(polygon, MarioBros.PPM);
            fixtureDef.friction = 0f;
            bodyPol.createFixture(fixtureDef);
        }
    }

    // chatgpt (revisar y entender)
    public PolygonShape convertPolygonToPolygonShape(Polygon polygon, float pixelsPerMeter) {
        PolygonShape polygonShape = new PolygonShape();

        // Get the transformed vertices from the LibGDX Polygon
        float[] vertices = polygon.getTransformedVertices();

        // Convert the vertices to Box2D's format (scaled to meters)
        Vector2[] box2dVertices = new Vector2[vertices.length / 2];
        for (int i = 0; i < vertices.length / 2; i++) {
            float x = vertices[i * 2] / pixelsPerMeter;
            float y = vertices[i * 2 + 1] / pixelsPerMeter;
            box2dVertices[i] = new Vector2(x, y);
        }

        // Ensure the vertices form a convex polygon (Box2D requires this)
        if (box2dVertices.length <= 8) { // Box2D supports up to 8 vertices
            float[] convexVertices = new float[box2dVertices.length * 2];
            for (int i = 0; i < box2dVertices.length; i++) {
                convexVertices[i * 2] = box2dVertices[i].x;
                convexVertices[i * 2 + 1] = box2dVertices[i].y;
            }
            polygonShape.set(convexVertices);
        } else {
            throw new IllegalArgumentException("Polygon has too many vertices for Box2D (max 8).");
        }

        return polygonShape;
    }


    @Override
    public void show() {

    }

    public void handleInput(float dt) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            personaje.b2body.applyLinearImpulse(new Vector2(0, 4f), personaje.b2body.getWorldCenter(), true);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && personaje.b2body.getLinearVelocity().x <= 2) {
            personaje.b2body.applyLinearImpulse(new Vector2(0.1f,0),personaje.b2body.getWorldCenter(),true);
        }if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && personaje.b2body.getLinearVelocity().x >= -2) {
            personaje.b2body.applyLinearImpulse(new Vector2(-0.1f,0),personaje.b2body.getWorldCenter(),true);
        }

    }

    public void update(float dt) {
        handleInput(dt);
        world.step(1 / 60f, 6, 2);

        gamecam.position.x=personaje.b2body.getPosition().x;
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

        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

    }

    @Override
    public void resize(int width, int height) {
        gameport.update(width, height);
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

    }
}
