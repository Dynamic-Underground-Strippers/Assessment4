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
import gameLogic.resource.DeleteConnection;
import gameLogic.resource.NewConnection;

//Responsible for checking whether the Connection is clicked.
public class DeleteConnectionClicked extends ClickListener {
    private DeleteConnection deleteConnection;
    private Context context;
    private boolean displayingMessage;

    public DeleteConnectionClicked(Context context, DeleteConnection deleteConnection) {
        this.deleteConnection = deleteConnection;
        this.context = context;
        displayingMessage = false;
    }

    @Override
    public void clicked(InputEvent event, float x, float y) {
        if (Game.getInstance().getState() == GameState.NORMAL) {

            // current player can't be passed in as it changes so find out current player at this instant
            Player currentPlayer = Game.getInstance().getPlayerManager().getCurrentPlayer();

            //Creates a dialog and a listener for the result of it
            DialogButtonClicked listener = new DialogButtonClicked(context, currentPlayer, deleteConnection);
            DialogResourceDeleteConnection dia = new DialogResourceDeleteConnection(deleteConnection, context.getSkin());
            dia.show(context.getStage());
            dia.subscribeClick(listener);
        }
    }
}
