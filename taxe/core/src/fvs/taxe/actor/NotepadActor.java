package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import fvs.taxe.TaxeGame;
import fvs.taxe.controller.NotepadController;

/**Type of Actor specifically for implementing the Top bar in the game GUI*/
public class NotepadActor extends Actor {

    private Texture notepadTexture;

    /**Width of obstacle Top Bar*/
    private float obstacleWidth = 0;

    /**Color for the controls, can be get and set*/
    private Color controlsColor = Color.LIGHT_GRAY;

    /**Color for obstacles, can be get and set*/
    private Color obstacleColor = Color.LIGHT_GRAY;

    /**Instantiation method sets up variables*/
    public NotepadActor(){
        super();
        notepadTexture = new Texture(Gdx.files.internal("notepad.png"));
        setSize(NotepadController.WIDTH, NotepadController.HEIGHT);
        setPosition(TaxeGame.WIDTH-NotepadController.WIDTH-15, TaxeGame.HEIGHT-NotepadController.HEIGHT-5);
        }


    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        batch.begin();
        batch.draw(notepadTexture, getX(), getY(), getWidth(), getHeight());
        batch.end();
        batch.begin();
    }

    /**Sets the color of the Obstacle bar*/
    public void setObstacleColor(Color color) {
        this.obstacleColor = color;
    }

    /**Sets the color of the Control bar*/
    public void setControlsColor(Color color) {
        this.controlsColor = color;
    }

    /**Sets the widthof the Obstacle bar*/
    public void setObstacleWidth(float width) {
        this.obstacleWidth = width;
    }
}
