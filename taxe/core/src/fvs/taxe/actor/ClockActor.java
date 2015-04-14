package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import fvs.taxe.TaxeGame;
import fvs.taxe.controller.NotepadController;

public class ClockActor extends Actor {
    private Texture clockTexture;
    public static final int WIDTH = 200;
    public static final int HEIGHT = (int) ((245f / 465f) * WIDTH);
    public static final int XPOS = (int) (TaxeGame.WIDTH - NotepadController.WIDTH - WIDTH - 30);
    public static final int YPOS = TaxeGame.HEIGHT - HEIGHT - 15;

    /**
     * Instantiation method sets up variables
     */
    public ClockActor() {
        super();
        clockTexture = new Texture(Gdx.files.internal("clock.png"));
        setSize(WIDTH, HEIGHT);
        setPosition(XPOS, YPOS);
    }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        batch.begin();
        batch.draw(clockTexture, getX(), getY(), getWidth(), getHeight());
        batch.end();
        batch.begin();
    }
}
