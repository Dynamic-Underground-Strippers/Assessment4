package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.map.IPositionable;
import gameLogic.resource.Train;

/**This class is a type of image specifically for creating Actors for Trains.*/
public class TrainActor extends Image {
	/**The width of a TrainActor in pixels.*/
    public static final int width = 36;
    
    /**The height of a TrainActor in pixels.*/
    public static final int height = 36;
    
    /**The train the TrainActor corresponds to.*/
    public Train train;

    /**The bounds of the TrainActor.*/
    private Rectangle bounds;
    
    /**The direction of the train. True = Left, False = Right.*/
    public boolean facingLeft;
    
    /**The previous x coordinate of the train (last frame).*/
    private float previousX;
    
    /**The default drawable for the train's left image.*/
    private Drawable leftDrawable;
    
    /**The default drawable for the train's right image.*/
    private Drawable rightDrawable;

    //changeRoute
    private boolean paused;
    private boolean recentlyPaused;
    /**The instantiation method sets up the drawables and bounds and positions the TrainActor.
     * @param train The train to base the TrainActor off.
     */
    public TrainActor(Train train) {
        super(new Texture(Gdx.files.internal(train.getLeftImage())));
        leftDrawable = getDrawable();
        rightDrawable = new Image(new Texture(Gdx.files.internal(train.getRightImage()))).getDrawable();

        IPositionable position = train.getPosition();

        train.setActor(this);
        this.train = train;
        setSize(width, height);
        bounds = new Rectangle();
        setPosition(position.getX() - width / 2, position.getY() - height / 2);
        previousX = getX();
        facingLeft = true;
    }

    @Override
    public void act (float delta) {
        if (Game.getInstance().getState() == GameState.ANIMATING) {
            super.act(delta);
            updateBounds();
            updateFacingDirection();
        }
    }

    /**Refreshes the bounds of the actor*/
    private void updateBounds() {
        bounds.set(getX(), getY(), getWidth(), getHeight());
    }
    
    /**Refreshes the orientation of the actor using it's current x coordinate and it's previous x coordinate.*/
    public void updateFacingDirection() {
        float currentX = getX();

        if(facingLeft && previousX < currentX) {
            setDrawable(rightDrawable);
            facingLeft = false;
        } else if(!facingLeft && previousX > currentX) {
            setDrawable(leftDrawable);
            facingLeft = true;
        }

        previousX = getX();
    }

    /**@returns the bounds of the TrainActor*/
    public Rectangle getBounds() {
        return bounds;
    }

    public void setPaused(boolean paused) {
        this.paused = paused;
    }

    public boolean isPaused() {
        return this.paused;
    }

    public boolean getPaused() {
        return this.paused;
    }

    public boolean isRecentlyPaused() {
        return recentlyPaused;
    }

    public void setRecentlyPaused(boolean recentlyPaused) {
        this.recentlyPaused = recentlyPaused;
    }
}
