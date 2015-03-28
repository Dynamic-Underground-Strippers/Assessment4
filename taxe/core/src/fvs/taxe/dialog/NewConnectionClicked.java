package fvs.taxe.dialog;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import fvs.taxe.controller.Context;
import fvs.taxe.dialog.DialogButtonClicked;
import fvs.taxe.dialog.DialogResourceNewConnection;
import gameLogic.Game;
import gameLogic.GameState;
import gameLogic.Player;
import gameLogic.resource.NewConnection;

//Responsible for checking whether the Connection is clicked.
public class NewConnectionClicked extends ClickListener {
    private NewConnection newConnection;
    private Context context;
    private boolean displayingMessage;

    public NewConnectionClicked(Context context, NewConnection newConnection) {
        this.newConnection = newConnection;
        this.context = context;
        displayingMessage = false;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (Game.getInstance().getState() == GameState.NORMAL) {

            // current player can't be passed in as it changes so find out current player at this instant
            Player currentPlayer = Game.getInstance().getPlayerManager().getCurrentPlayer();

            //Creates a dialog and a listener for the result of it
            DialogButtonClicked listener = new DialogButtonClicked(context, currentPlayer, newConnection);
            DialogResourceNewConnection dia = new DialogResourceNewConnection(newConnection, context.getSkin());
            dia.show(context.getStage());
            dia.subscribeClick(listener);
        }
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor trainActor) {
        //This is used for mouseover events for Obstacles
        //This shows the message if there is not one currently being displayed
        if (!displayingMessage) {
            displayingMessage = true;
            if (Game.getInstance().getState() == GameState.NORMAL) {
                context.getTopBarController().displayMessage("Create a new connection on the map", Color.BLACK);


            }
        }
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor trainActor) {
        //This is used for mouseover events for Obstacles
        //This hides the message currently in the topBar if one is being displayed
        if (displayingMessage) {
            displayingMessage = false;
            if (Game.getInstance().getState() == GameState.NORMAL) {
                //If the game state is normal then the topBar is cleared by passing it an empty string to display for 0 seconds
                context.getTopBarController().clearMessage();
            }
        }
    }
}
