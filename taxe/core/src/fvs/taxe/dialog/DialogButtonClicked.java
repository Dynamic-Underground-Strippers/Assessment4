package fvs.taxe.dialog;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
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
import gameLogic.resource.Train;

/**This class is a specific type fo ResourceDialogueClickListener for dialogue Buttons.*/
public class DialogButtonClicked implements ResourceDialogClickListener {
	
	/**The context of the game.*/
    private Context context;
    
    /**The current player in the game*/
    private Player currentPlayer;
    
    /**The train the Dialogue represents.*/
    private Train train;

    /**Instantiation
     * @param context The game Context.
     * @param player The current player for the dialogue.
     * @param train The train for the dialogue.
     */
    public DialogButtonClicked(Context context, Player player, Train train) {
        this.currentPlayer = player;
        this.train = train;
        this.context = context;
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
                TrainController trainController = new TrainController(context);
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
        }
    }
}
