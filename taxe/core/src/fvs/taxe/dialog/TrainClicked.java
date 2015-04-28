package fvs.taxe.dialog;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.resource.Train;

/**This is a special type of ClickListener for when a train is clicked.*/
public class TrainClicked extends ClickListener {
	/**The game context.*/
    private Context context;
    
    /**The train that the click listener corresponds to.*/
    private Train train;

    /**Instantiation method.
     * @param context The context of the game.
     * @param train The train the TrainClicked corresponds to.
     */
    public TrainClicked(Context context, Train train) {
        this.train = train;
        this.context = context;
    }

    /**This method is called when the TrainClicked is Clicked. The GUI is updated accordingly.*/
    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (Game.getInstance().getState() != GameState.NORMAL) return;

        // current player can't be passed in as it changes so find out current player at this instant
        Player currentPlayer = Game.getInstance().getPlayerManager().getCurrentPlayer();

        if (!train.isOwnedBy(currentPlayer)) {
//            context.getNotepadController().displayFlashMessage("Opponent's " + train.getName() + ". Speed: " + train.getSpeed(), Color.RED, 2);
            return;
        }

        if (train.getFinalDestination() == null) {
//            context.getNotepadController().displayFlashMessage("Your " + train.getName() + ". Speed: " + train.getSpeed(), Color.BLACK, 2);
        } else {
//            context.getNotepadController().displayFlashMessage("Your " + train.getName() + ". Speed: " + train.getSpeed() + ". Destination: " + train.getFinalDestination().getName(), Color.BLACK, 2);
        }
        DialogButtonClicked listener = new DialogButtonClicked(context, currentPlayer, train);
        DialogResourceTrain dia = new DialogResourceTrain(context, train, context.getSkin(), train.getPosition() != null);
        dia.show(context.getStage());
        dia.subscribeClick(listener);
    }

}
