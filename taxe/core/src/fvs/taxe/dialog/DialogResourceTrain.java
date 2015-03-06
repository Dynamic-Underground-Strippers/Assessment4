package fvs.taxe.dialog;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import fvs.taxe.Button;
import fvs.taxe.controller.Context;
import gameLogic.GameState;
import gameLogic.resource.Train;

public class DialogResourceTrain extends Dialog {
	/**List of Click Listeners. When a button is clicked, each Click Listener is notified.*/
    private List<ResourceDialogClickListener> clickListeners = new ArrayList<ResourceDialogClickListener>();
    
    /**The Game context.*/
	private Context context;

	/**The instantiation sets up the dialogue for a train resource.
	 * @param context The context of the game.
	 * @param train The train resource in use.
	 * @param skin The skin for the GUI.
	 * @param trainPlaced Whether the train has been placed.
	 */
    public DialogResourceTrain(Context context, Train train, Skin skin, boolean trainPlaced) {
        super(train.toString(), skin);
        this.context = context;
        context.getGameLogic().setState(GameState.WAITING);
        text("What do you want to do with this train?");

        button("Cancel", "CLOSE");
        button("Delete", "DELETE");

        if (!trainPlaced) {
            button("Place at a station", "PLACE");
        } else if(!train.isMoving()) {
            button("Choose a route", "ROUTE");
        } else if (train.getRoute() != null) {
            //If the train has a route then generate button to change the route
            button("Change route", "CHANGE_ROUTE");
        }
    }

    @Override
    public Dialog show (Stage stage) {
        show(stage, null);
        setPosition(Math.round((stage.getWidth() - getWidth()) / 2), Math.round((stage.getHeight() - getHeight()) / 2));
        return this;
    }

    @Override
    public void hide () {
        hide(null);
    }

    /**This method is called when a button is clicked. All of the registered click listeners are notified.*/
    private void clicked(Button button) {
        for(ResourceDialogClickListener listener : clickListeners) {
            listener.clicked(button);
        }
    }

    /**This method adds a new ClickListener. When a button is clicked, the new listener will be notified.*/
    public void subscribeClick(ResourceDialogClickListener listener) {
        clickListeners.add(listener);
    }

    @Override
    protected void result(Object obj) {
    	context.getGameLogic().setState(GameState.NORMAL);
        if (obj == "CLOSE") {
            this.remove();
        } else if (obj == "DELETE") {
            clicked(Button.TRAIN_DELETE);
        } else if(obj == "PLACE") {
            clicked(Button.TRAIN_PLACE);
        } else if(obj == "ROUTE") {
            clicked(Button.TRAIN_ROUTE);
        } else if (obj == "CHANGE_ROUTE") {
            clicked(Button.TRAIN_CHANGE_ROUTE);
        }
        
    }
}
