package fvs.taxe.actor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
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

<<<<<<< HEAD
    /**Indicates whether the train is paused at a blocked connection or not.*/
    private boolean paused;

    /**Indicates whether the train has recently been paused at a blocked connection.*/
    private boolean recentlyPaused;

    private Context context;

=======
    //changeRoute
    private boolean paused;
    private boolean recentlyPaused;
>>>>>>> master
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
                //If there is a collision then the user is informed, the two trains destroyed and the connection that they collided on is blocked for 5 turns.
                context.getTopBarController().displayFlashMessage("Two trains collided. They were both destroyed", Color.BLACK, Color.RED, 4);
                Game.getInstance().getMap().blockConnection(train.getLastStation(), train.getNextStation(), 5);

                collision.getActor().remove();
                collision.getPlayer().removeResource(collision);
                train.getPlayer().removeResource(train);
                this.remove();
            }

        } else if (this.paused) {
            //Everything inside this block ensures that the train does not move if the paused variable is set to true.
            //This ensures that trains do not move through blocked connections when they are not supposed to.

            //find station train most recently passed
            Station station = train.getHistory().get(train.getHistory().size() - 1).getFirst();
//            Station station = Game.getInstance().getMap().getStationByName(stationName);

            // find index of this within route
            int index = train.getRoute().indexOf(station);

            // find next station
            Station nextStation = train.getRoute().get(index + 1);

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

<<<<<<< HEAD
    public Train collided() {
        //The aim of this function is to check whether the train represented by the actor has collided with any other trains on the board
        Station last = train.getLastStation();
        Station next = train.getNextStation();
        if (train.getPosition().getX() == -1 && !paused) {
            //if this train is moving;
            for (Player player : Game.getInstance().getPlayerManager().getAllPlayers()) {
                for (Train otherTrain : player.getTrains()) {
                    //This checks every train that is currently present within the game
                    if (!otherTrain.equals(train)) {
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

                                    float difX = Math.abs(this.getX() - otherTrain.getActor().getX());
                                    float difY = Math.abs(this.getX() - otherTrain.getActor().getX());
                                    float difFactor = difX * difY;


                                    if ((difFactor < 4) && !((this.recentlyPaused) || (otherTrain.getActor().isRecentlyPaused()))) {
                                    //if ((this.bounds.overlaps(otherTrain.getActor().getBounds())) && !((this.recentlyPaused) || (otherTrain.getActor().isRecentlyPaused()))) {
                                        //Checks whether the two trains are recently paused, if either of them are then no collision should occur
                                        //This prevents the issue of two paused trains crashing when they shouldn't
                                        //There is still the potential issue of two blocked trains colliding when they shouldn't, as it is impossible to know which connection a blocked train will occupy. i.e when one train is rerouted but not the other

                                        //TEMP??? - ONLY COLLIDE WHEN TRAINS ARE IN SAME DIRECTION
                                        //TODO: DECIDE WHETHER THIS IS TEMPORARY OR PERMANENT CHANGE
                                        if (this.facingLeft == otherTrain.getActor().isFacingLeft())
                                            return otherTrain;

                                        //This is slightly limiting as it only allows two trains to collide with each other, whereas in theory more than 2 could collide, this is however very unlikely and due to complications
                                        //not necessary to factor in to our implementation at this stage. If you need to add more trains then you would have to build up a list of collided trains and then return it.
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
    public boolean isPaused() { return this.paused; }

    public boolean isRecentlyPaused() { return this.recentlyPaused; }

    public void setPaused(Boolean bool) { this.paused = bool; }

    public void setRecentlyPaused(Boolean bool) { this.recentlyPaused = bool; }

    public boolean isFacingLeft() {return this.facingLeft; }
=======
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
>>>>>>> master
}
