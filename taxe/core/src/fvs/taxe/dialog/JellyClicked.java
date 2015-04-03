package fvs.taxe.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.controller.Context;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.resource.Jelly;

/**This is a special type of ClickListener for when a jelly is clicked.*/
public class JellyClicked extends ClickListener {
    /**The game context.*/
    private Context context;

    /**The jelly that the click listener corresponds to.*/
    private Jelly jelly;

    /**Instantiation method.
     * @param context The context of the game.
     * @param jelly The jelly the TrainClicked corresponds to.
     */
    public JellyClicked(Context context, Jelly jelly) {
        this.jelly = jelly;
        this.context = context;
    }

    /**This method is called when the TrainClicked is Clicked. The GUI is updated accordingly.*/
    /** @Override
    * jelly do not require to be clicked
    public void clicked(InputEvent event, float x, float y) {
        if (Game.getInstance().getState() != GameState.NORMAL) return;

        // current player can't be passed in as it changes so find out current player at this instant
        Player currentPlayer = Game.getInstance().getPlayerManager().getCurrentPlayer();

        if (!jelly.isOwnedBy(currentPlayer)) {
            context.getTopBarController().displayFlashMessage("Opponent's " + jelly.getName() + ". Speed: " + jelly.getSpeed(), Color.RED, 2);
            return;
        }

        if (jelly.getFinalDestination() == null) {
            context.getTopBarController().displayFlashMessage("Your " + jelly.getName() + ". Speed: " + jelly.getSpeed(), Color.BLACK, 2);
        } else {
            context.getTopBarController().displayFlashMessage("Your " + jelly.getName() + ". Speed: " + jelly.getSpeed() + ". Destination: " + jelly.getFinalDestination().getName(), Color.BLACK, 2);
        }
        DialogButtonClicked listener = new DialogButtonClicked(context, currentPlayer, jelly);
        DialogResourceJelly dia = new DialogResourceJelly(context, jelly, context.getSkin(), jelly.getPosition() != null);
        dia.show(context.getStage());
        dia.subscribeClick(listener);
    }**/

}
