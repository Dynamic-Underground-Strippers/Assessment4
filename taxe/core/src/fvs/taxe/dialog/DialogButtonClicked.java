package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.utils.Align;
import fvs.taxe.Button;
import fvs.taxe.StationClickListener;
import fvs.taxe.actor.TrainActor;
import fvs.taxe.controller.Context;
import fvs.taxe.controller.StationController;
import fvs.taxe.controller.TrainController;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.map.CollisionStation;
import gameLogic.map.Station;
import gameLogic.resource.NewConnection;
import gameLogic.resource.Train;

/**This class is a specific type fo ResourceDialogueClickListener for dialogue Buttons.*/
public class DialogButtonClicked implements ResourceDialogClickListener {
	
	/**The context of the game.*/
    private Context context;
    
    /**The current player in the game*/
    private Player currentPlayer;
    
    /**The train the Dialogue represents.*/
    private Train train;

    private NewConnection newConnection;

    /**Instantiation
     * @param context The game Context.
     * @param player The current player for the dialogue.
     * @param train The train for the dialogue.
     */
    public DialogButtonClicked(Context context, Player player, Train train) {
        this.currentPlayer = player;
        this.train = train;
        this.context = context;
        this.newConnection = null;
    }

    public DialogButtonClicked(Context context, Player player, NewConnection newConnection) {
        this.currentPlayer = player;
        this.train = null;
        this.context = context;
        this.newConnection = newConnection;
    }

    /**When a button is clicked, this method is called. It acts according to the case of the button.*/
    @Override
    public void clicked(Button button) {
        switch (button) {
            case TRAIN_DELETE:
                currentPlayer.removeResource(train);
                break;
            case TRAIN_PLACE:
                Pixmap pixmap = new Pixmap(Gdx.files.internal(train.getCursorImage()));
                Gdx.input.setCursorImage(pixmap, 20, 25); 
                pixmap.dispose();

                Game.getInstance().setState(GameState.PLACING);
                final TrainController trainController = new TrainController(context);
                trainController.setTrainsVisible(null, false);

                StationController.subscribeStationClick(new StationClickListener() {
                    @Override
                    public void clicked(Station station) {
                    	if(station instanceof CollisionStation) {
                    		context.getTopBarController().displayFlashMessage("Trains cannot be placed at junctions.", Color.RED);
                    		return;
                    	}
                    	
                        train.setPosition(station.getLocation());
                        train.addHistory(station, Game.getInstance().getPlayerManager().getTurnNumber());

                        Gdx.input.setCursorImage(null, 0, 0);

                        TrainController trainController = new TrainController(context);
                        TrainActor trainActor = trainController.renderTrain(train);
                        trainController.setTrainsVisible(null, true);
                        train.setActor(trainActor);

                        StationController.unsubscribeStationClick(this);
                        Game.getInstance().setState(GameState.NORMAL);
                    }
                });

                break;
            case TRAIN_ROUTE:
                context.getRouteController().begin(train);

                break;

            case TRAIN_CHANGE_ROUTE:
                context.getRouteController().begin(train);

                break;

            case NEWCONNECTION_DROP:
                //Removes the obstacle from the current player's inventory if they click the OBSTACLE_DROP button
                currentPlayer.removeResource(newConnection);
                break;

            case NEWCONNECTION_CREATE: {
                /*
                //Sets the cursor to be the one used to indicate placing a blockage
                Pixmap pixmap = new Pixmap(Gdx.files.internal("BlockageCursor.png"));
                Gdx.input.setCursorImage(pixmap, 0, 0); // these numbers will need tweaking
                pixmap.dispose(); */

                //Indicates that a resource is currently being placed and to hide all trains
                //While it would be useful to see trains while placing an obstacle, this was done to remove the possibility of trains preventing the user being able to click a node
                Game.getInstance().setState(GameState.PLACING_RESOURCE);
                final TrainController trainControl = new TrainController(context);
                trainControl.setTrainsVisible(null, false);
                context.getTopBarController().displayMessage("Creating New Connection", Color.BLACK);

                //Creates a clickListener for when a station is clicked
                final StationClickListener stationListener = new StationClickListener() {
                    @Override
                    public void clicked(Station station) {

                        //If the station clicked is the first one to be chosen by the user
                        if (newConnection.getStation1() == null) {

                            //Sets the first station to be the one that the user selects
                            newConnection.setStation1(station);

                        } else {
                            //Sets the second station of the blockage to be the one that the user selects once they have selected the first one
                            newConnection.setStation2(station);

                            //Checks whether a connection exists between the two stations
                            if (!(context.getGameLogic().getMap().doesConnectionExist(newConnection.getStation1().getName(), newConnection.getStation2().getName()))) {

                                //Create connection and actor then check if connection overlaps others
                                Boolean bool = newConnection.use(context);

                                if (bool){
                                    //new connection successfully added
                                    //The obstacle is removed from the player's inventory as it has been used
                                    currentPlayer.removeResource(newConnection);
                                } else {
                                    //Informs the player that their selection is invalid and cancels placement
                                    Dialog dia = new Dialog("Invalid Selection", context.getSkin());
                                    dia.text("That connection would overlap other tracks." +
                                            "\nPlease use the New Connection resource again.").align(Align.center);
                                    dia.button("OK", "OK");
                                    dia.show(context.getStage());
                                    newConnection.setStation1(null);
                                    newConnection.setStation2(null);
                                }

                            } else {

                                //Informs the player that their selection is invalid and cancels placement
                                Dialog dia = new Dialog("Invalid Selection", context.getSkin());
                                dia.text("You have selected two stations which are already connected." +
                                        "\nPlease use the New Connection resource again.").align(Align.center);
                                dia.button("OK", "OK");
                                dia.show(context.getStage());
                                newConnection.setStation1(null);
                                newConnection.setStation2(null);
                            }
                            //This code runs regardless of whether the placement was successful, this returns the game to its normal state

                            //Resets the topBar
                            context.getTopBarController().displayFlashMessage("", Color.BLACK);

                            //Unsubscribes from the StationClickListener as this would cause a lot of errors and unexpected behaviour is not called from the correct context
                            StationController.unsubscribeStationClick(this);

                            //Resets the cursor to the normal one
                            Gdx.input.setCursorImage(null, 0, 0);
                            context.getGameLogic().setState(GameState.NORMAL);

                            //Sets all moving trains to be visible
                            trainControl.setTrainsVisible(null, true);
                        }
                    }
                };
                final InputListener keyListener = new InputListener() {
                    @Override
                    public boolean keyDown(InputEvent event, int keycode) {
                        //If the Escape key is pressed while placing an obstacle then it is cancelled
                        if (keycode == Input.Keys.ESCAPE) {
                            //Makes all trains visible
                            TrainController trainController = new TrainController(context);
                            trainController.setTrainsVisible(null, true);

                            //Resets cursor
                            Gdx.input.setCursorImage(null, 0, 0);

                            //Unsubscribes from the StationClickListener as this would cause a lot of errors and unexpected behaviour is not called from the correct context
                            StationController.unsubscribeStationClick(stationListener);
                            Game.getInstance().setState(GameState.NORMAL);

                            //Resets the topBar
                            context.getTopBarController().clearMessage();

                            //Removes itself from the keylisteners of the game as otherwise there would be a lot of null pointer exceptions and unintended behaviour
                            context.getStage().removeListener(this);
                        }
                        //keyDown requires you to return the boolean true when the function has completed, so this ends the function
                        return true;
                    }
                };

                //Adds the listeners to their relevant entities
                context.getStage().addListener(keyListener);
                StationController.subscribeStationClick(stationListener);

                break;
            }
        }
    }
}
