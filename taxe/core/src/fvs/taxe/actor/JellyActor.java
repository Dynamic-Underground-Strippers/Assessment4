package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.map.IPositionable;
import gameLogic.map.Station;
import gameLogic.resource.Jelly;
import gameLogic.resource.Train;


/**This class is a type of image specifically for creating Actors for Trains.*/
public class JellyActor extends Image {
    /**The width of a TrainActor in pixels.*/
    public static final int width = 36;

    /**The height of a TrainActor in pixels.*/
    public static final int height = 36;

    /**The train the TrainActor corresponds to.*/
    public Jelly jelly;

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

    /**Indicates whether the train is paused at a blocked connection or not.*/
    private boolean paused;

    /**Indicates whether the train has recently been paused at a blocked connection.*/
    private boolean recentlyPaused;

    private Context context;

    /**The instantiation method sets up the drawables and bounds and positions the TrainActor.
     * @param jelly The train to base the TrainActor off.
     */
    public JellyActor(Jelly jelly,Context context) {
        super(new Texture(Gdx.files.internal(jelly.getLeftImage())));
        leftDrawable = getDrawable();
        rightDrawable = new Image(new Texture(Gdx.files.internal(jelly.getRightImage()))).getDrawable();

        IPositionable position = jelly.getPosition();
        this.context = context;
        jelly.setActor(this);
        this.jelly = jelly;
        setSize(width, height);
        bounds = new Rectangle();
        setPosition(position.getX() - width / 2, position.getY() - height / 2);
        previousX = getX();
        facingLeft = true;
        paused = false;
        recentlyPaused = false;
    }

    @Override
    public void act (float delta) {
        if ((Game.getInstance().getState() == GameState.ANIMATING) && (!this.paused)) {
            super.act(delta);
            updateBounds();
            updateFacingDirection();

            Train collision = collided();
            if (collision != null) {




                //no need to remove the other train, no need to remove jelly
               //Instead the player that owned the train is set to skip their turn
                collision.getPlayer().setSkip(true);


            }

        } else if (this.paused) {
            //Everything inside this block ensures that the train does not move if the paused variable is set to true.
            //This ensures that trains do not move through blocked connections when they are not supposed to.

            //find station train most recently passed
            Station station = jelly.getHistory().get(jelly.getHistory().size() - 1).getFirst();
//            Station station = Game.getInstance().getMap().getStationByName(stationName);

            // find index of this within route
            int index = jelly.getRoute().indexOf(station);

            // find next station
            Station nextStation = jelly.getRoute().get(index + 1);

            // check if connection is blocked, if not, unpause
            if (!Game.getInstance().getMap().isConnectionBlocked(station, nextStation)) {
                this.paused = false;
                this.recentlyPaused = true;
            }
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

    /**returns the bounds of the TrainActor*/
    public Rectangle getBounds() {
        return bounds;
    }

    public Train collided() {
        //The aim of this function is to check whether the train represented by the actor has collided with any other trains on the board
        Station last = jelly.getLastStation();
        Station next = jelly.getFinalDestination();
        if (jelly.getPosition().getX() == -1 && !paused) {
            //if this train is moving;
            for (Player player : Game.getInstance().getPlayerManager().getAllPlayers()) {
                for (Train otherTrain : player.getTrains()) {
                    //This checks every train that is currently present within the game
                    if (!otherTrain.equals(jelly)) {
                        //don't check if collided with self
                        if (otherTrain.getPosition() != null) {
                            //Checks if the other train has been placed on the map
                            if (otherTrain.getPosition().getX() == -1 && !otherTrain.getActor().isPaused()) {
                                //if other train moving
                                //This is because the position of the train when it is in motion (i.e travelling along its route) is (-1,-1) as that is how FVS decided to implement it
                                //It is necessary to check whether this is true as if the train is not in motion then it does not have an actor, hence otherTrain.getActor() would cause a null point exception.

                                if ((otherTrain.getNextStation() == next && otherTrain.getLastStation() == last)
                                        || (otherTrain.getNextStation() == last && otherTrain.getLastStation() == next)) {
                                    //check if trains on same connection
                                    if (otherTrain.getActor().getBounds().overlaps(bounds)) {
                                        return otherTrain;
                                    }

                                }
                            }
                        }
                    }
                }
            }
        }
        return null;
    }
}
