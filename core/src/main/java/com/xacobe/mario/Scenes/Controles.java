package com.xacobe.mario.Scenes;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.xacobe.mario.MarioBros;

public class Controles {
    public Stage stage;
    private Viewport viewport;

    private boolean jumpPressed, attackPressed, moveRight, moveLeft, crouch;

    public Controles(SpriteBatch batch) {
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, batch);
        Gdx.input.setInputProcessor(stage);

        Table table = new Table();
        table.bottom().left();
        table.setFillParent(true);

        // Botones de movimiento
        ImageButton leftButton = createButton("left.png", 80, 80);
        ImageButton rightButton = createButton("right.png", 80, 80);
        ImageButton crouchButton = createButton("down.png", 80, 80);


        leftButton.addListener(new MovementListener(() -> moveLeft = true, () -> moveLeft = false));
        rightButton.addListener(new MovementListener(() -> moveRight = true, () -> moveRight = false));
        crouchButton.addListener(new MovementListener(() -> crouch = true, () -> crouch = false));

        // Botones de acción (Saltar y Atacar)
        ImageButton jumpButton = createButton("jump.png", 40, 40);
        ImageButton attackButton = createButton("attack.png", 40, 40);

        jumpButton.addListener(new MovementListener(() -> jumpPressed = true, () -> jumpPressed = false));
        attackButton.addListener(new MovementListener(() -> attackPressed = true, () -> attackPressed = false));

        // Organizar botones en la tabla
        table.add(leftButton).size(80, 80).padRight(2);
        table.add(rightButton).size(80, 80);
        table.row().padTop(-10);
        table.add(crouchButton).size(80, 80).colspan(2);

        Table actionTable = new Table();
        actionTable.bottom().right();
        actionTable.setFillParent(true);
        actionTable.add(attackButton).size(40, 40).padRight(20).padBottom(20);
        actionTable.add(jumpButton).size(40, 40).padRight(40).padBottom(100);

        stage.addActor(table);
        stage.addActor(actionTable);
    }

    private ImageButton createButton(String texturePath, float width, float height) {
        Texture texture = new Texture(texturePath);
        TextureRegionDrawable drawable = new TextureRegionDrawable(texture);

        ImageButton.ImageButtonStyle style = new ImageButton.ImageButtonStyle();
        style.imageUp = drawable;
        style.imageDown = drawable;
        style.up = null;
        style.down = null;

        ImageButton button = new ImageButton(style);
        button.setSize(width, height);
        button.getImage().setSize(width, height);
        button.getImageCell().size(width, height);
        button.setTransform(true);
        button.setScale(1.0f);

        return button;
    }

    private static class MovementListener extends InputListener {
        private final Runnable onPress, onRelease;

        public MovementListener(Runnable onPress, Runnable onRelease) {
            this.onPress = onPress;
            this.onRelease = onRelease;
        }

        @Override
        public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
            onPress.run();
            return true;
        }

        @Override
        public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
            onRelease.run();
        }
    }

    public void render(SpriteBatch batch) {
        stage.draw();
    }

    public void update() {
        stage.act();
    }

    public boolean isJumpPressed() {
        return jumpPressed;
    }

    public boolean isAttackPressed() {
        return attackPressed;
    }

    public boolean isMoveRight() {
        return moveRight;
    }

    public boolean isMoveLeft() {
        return moveLeft;
    }

    public boolean isCrouching() {
        return crouch;
    }

    public void resize(int width, int height) {
        viewport.update(width, height);
    }
}
